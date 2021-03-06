package br.edu.utfpr.cm.JGitMinerWeb.services.metric;

import br.edu.utfpr.cm.JGitMinerWeb.dao.AuxCodeChurn;
import br.edu.utfpr.cm.JGitMinerWeb.dao.AuxUser;
import br.edu.utfpr.cm.JGitMinerWeb.dao.FileDAO;
import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.dao.PairFileDAO;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrixNode;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.UserCommentedSamePairOfFileInAllDateServices;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.UserCommentedSamePairOfFileInDateServices;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.UserCommentedSamePairOfFileInNumberServices;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxFileFile;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxUserUser;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.AuxFileFileMetrics;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.centrality.BetweennessCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.centrality.ClosenessCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.centrality.DegreeCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.centrality.EigenvectorCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.ego.EgoMeasure;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.ego.EgoMeasureCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.global.GlobalMeasure;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.global.GlobalMeasureCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.structuralholes.StructuralHolesCalculator;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.structuralholes.StructuralHolesMeasure;
import br.edu.utfpr.cm.JGitMinerWeb.util.JsfUtil;
import br.edu.utfpr.cm.JGitMinerWeb.util.JungExport;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Rodrigo T. Kuroda
 */
public class PairFileGlobalCommunicationSingleEdgeSNAMetricsInDateServices extends AbstractMetricServices {

    private EntityRepository repository;

    public PairFileGlobalCommunicationSingleEdgeSNAMetricsInDateServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public PairFileGlobalCommunicationSingleEdgeSNAMetricsInDateServices(GenericDao dao, EntityMatrix matrix, Map params, OutLog out) {
        super(dao, matrix, params, out);
    }

    private Integer getIntervalOfMonths() {
        return getIntegerParam("intervalOfMonths");
    }

    private Date getBeginDate() {
        if (getMatrix().getClassServicesName().equals(UserCommentedSamePairOfFileInAllDateServices.class.getName())) {
            return getDateParam("matrixBeginDate");
        }
        return getDateParam("beginDate");
    }

    private Date getEndDate() {
        if (getMatrix().getClassServicesName().equals(UserCommentedSamePairOfFileInAllDateServices.class.getName())) {
            return getDateParam("matrixEndDate");
        }
        return getDateParam("endDate");
    }

    public Date getFutureBeginDate() {
        return getDateParam("futureBeginDate");
    }

    public Date getFutureEndDate() {
        return getDateParam("futureEndDate");
    }

    @Override
    public void run() {

        if (getMatrix() == null
                || !getAvailableMatricesPermitted().contains(getMatrix().getClassServicesName())) {
            throw new IllegalArgumentException("Selecione uma matriz gerada pelo Services: " + getAvailableMatricesPermitted());
        }

        repository = getRepository();

        if (repository == null) {
            throw new IllegalArgumentException("Não foi possível encontrar o repositório utilizado nesta matriz.");
        }

        Date futureBeginDate = getFutureBeginDate();
        Date futureEndDate = getFutureEndDate();
        
        Date beginDate = getBeginDate();
        Date endDate = getEndDate();

        if (futureBeginDate == null && futureEndDate == null) {
            if (getIntervalOfMonths() == null || getIntervalOfMonths() == 0) {
                throw new IllegalArgumentException("A matriz selecionada não possui parâmetro de Interval Of Months, informe Future Begin Date e Future End Date.");
            }
            futureBeginDate = (Date) getEndDate().clone();
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) futureBeginDate.clone());
            cal.add(Calendar.MONTH, getIntervalOfMonths());
            futureEndDate = cal.getTime();
        }

        params.put("futureBeginDate", futureBeginDate);
        params.put("futureEndDate", futureEndDate);

        // user | file | file2 | user2 | weigth
        out.printLog("Iniciado cálculo da métrica de matriz com " + getMatrix().getNodes().size() + " nodes. Parametros: " + params);

        out.printLog("Iniciando construÃ§ão da rede.");

        final Map<AuxFileFile, Set<String>> commitersPairFile = new HashMap<>();
        final Map<String, Integer> edgesWeigth = new HashMap<>();
        
        // rede de comunicaÃ§ão global, com todos pares de arquivos
        DirectedSparseGraph<String, String> graph = new DirectedSparseGraph<>();
        
        // rede de comunicaÃ§ão de cada par de arquivo
        Map<AuxFileFile, DirectedSparseGraph<String, String>> pairFileNetwork = new HashMap<>();
        
        int countIgnored = 0;
        FileDAO fileDAO = new FileDAO(dao);
        PairFileDAO pairFileDAO = new PairFileDAO(dao);
        Long pullRequestsSize = pairFileDAO
                .calculeNumberOfPullRequest(repository,
                        null, null, futureBeginDate, futureEndDate, true);
        
        System.out.println("Number of all pull requests: " + pullRequestsSize);
        
        //Map<String, Long> futurePullRequest = new HashMap<>();
        
        // construindo a rede de comunicaÃ§ão para cada par de arquivo (desenvolvedores que comentaram)
        int nodesSize = getMatrix().getNodes().size();
        int count = 0;
        Set<AuxFileFile> pairFilesSet = new HashSet<>();
        for (int i = 0; i < getMatrix().getNodes().size(); i++) {
            if (count++ % 100 == 0 || count == nodesSize) {
                System.out.println(count + "/" + nodesSize);
            }
            EntityMatrixNode node = getMatrix().getNodes().get(i);
            String[] columns = node.getLine().split(JsfUtil.TOKEN_SEPARATOR);

            AuxFileFile pairFile = new AuxFileFile(columns[1], columns[2]);
            pairFilesSet.add(pairFile);
            // ignora %README%, %Rakefile, %CHANGELOG%, %Gemfile%, %.gitignore
//            if (isIgnored(pairFile.getFileName())
//                    || isIgnored(pairFile.getFileName2())) {
//                out.printLog("Ignoring " + pairFile);
//                countIgnored++;
//                continue;
//            }
            
//            Long pairFileNumberOfPullrequestOfPairFuture;
//            if (futurePullRequest.containsKey(pairFile.toString())) {
//                pairFileNumberOfPullrequestOfPairFuture = futurePullRequest.get(pairFile.toString());
//            } else {
//                pairFileNumberOfPullrequestOfPairFuture = pairFileDAO
//                    .calculeNumberOfPullRequest(repository,
//                            pairFile.getFileName(), pairFile.getFileName2(),
//                            futureBeginDate, futureEndDate, true);
//                futurePullRequest.put(pairFile.toString(), pairFileNumberOfPullrequestOfPairFuture);
//            }
            
//            Double supportPairFile = numberOfAllPullrequestFuture == 0 ? 0d : 
//                    pairFileNumberOfPullrequestOfPairFuture.doubleValue() /
//                    numberOfAllPullrequestFuture.doubleValue();
            
            
            // minimum support is 0.01, ignore file if lower than this (0.01)
//            if (supportPairFile < Double.valueOf(0.01d)) {
//            if (pairFileNumberOfPullrequestOfPairFuture < 2) {
//                out.printLog("Ignoring " + pairFile + ": future pull requests " + pairFileNumberOfPullrequestOfPairFuture);
//                countIgnored++;
//                continue;
//            }
            
            String commiter1 = columns[0];
            String commiter2 = columns[3];
            
            /**
             * Extract all distinct developer that commit a pair of file
             */
            if (commitersPairFile.containsKey(pairFile)) {
                Set<String> commiters = commitersPairFile.get(pairFile);
                commiters.add(commiter1);
                commiters.add(commiter2);
            } else {
                Set<String> commiters = new HashSet<>();
                commiters.add(commiter1);
                commiters.add(commiter2);
                commitersPairFile.put(pairFile, commiters);
            }

            // adiciona conforme o peso
//            String edgeName = pairFile.getFileName() + "-" + pairFile.getFileName2() + "-" + i;
            AuxUserUser pairUser = new AuxUserUser(columns[0], columns[3]);
            
            /* Sum commit for each pair file that the pair devCommentter has commited. */
            // user > user2 - directed edge
            if (edgesWeigth.containsKey(pairUser.toStringUserAndUser2())) {
                // edgeName = user + user2
                edgesWeigth.put(pairUser.toStringUserAndUser2(), edgesWeigth.get(pairUser.toStringUserAndUser2()) + Integer.valueOf(columns[4]));
//            // for undirectional graph
//            } else if (edgesWeigth.containsKey(pairUser.toStringUser2AndUser())) {
//                // edgeName = user2 + user - undirected edge
//                edgesWeigth.put(pairUser.toStringUser2AndUser(), edgesWeigth.get(pairUser.toStringUser2AndUser()) + Integer.valueOf(columns[4]));
            } else {
                edgesWeigth.put(pairUser.toStringUserAndUser2(), Integer.valueOf(columns[4]));
            }
            
            if (!graph.containsVertex(pairUser.getUser()) 
                    || !graph.containsVertex(pairUser.getUser2()) 
                    || !graph.containsEdge(pairUser.toStringUserAndUser2())) {
                graph.addEdge(pairUser.toStringUserAndUser2(), pairUser.getUser(), pairUser.getUser2(), EdgeType.DIRECTED);
            }
                
            // check if network already created
            if (pairFileNetwork.containsKey(pairFile)) {
                pairFileNetwork.get(pairFile)
                        .addEdge(pairUser.toStringUserAndUser2(), pairUser.getUser(), pairUser.getUser2(), EdgeType.DIRECTED);
            } else {
                DirectedSparseGraph<String, String> graphMulti
                        = new DirectedSparseGraph<>();
                graphMulti.addEdge(pairUser.toStringUserAndUser2(), pairUser.getUser(), pairUser.getUser2(), EdgeType.DIRECTED);
                pairFileNetwork.put(pairFile, graphMulti);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        JungExport.exportToImage(graph, "C:/Users/a562273/Desktop/networks/", 
                "Single" + format.format(beginDate) + " a " + format.format(endDate));
        
        out.printLog("Número de pares de arquivos ignoradoa: " + countIgnored);
        
        out.printLog("Número de autores de comentários (commenters): " + graph.getVertexCount());
        out.printLog("Número de pares de arquivos: " + commitersPairFile.size());
        out.printLog("Número de pares de arquivos new: " + pairFilesSet.size());
        out.printLog("Iniciando cálculo das métricas.");

        Set<AuxFileFileMetrics> fileFileMetrics = new HashSet<>();
        
        out.printLog("Calculando metricas SNA...");

        GlobalMeasure global = GlobalMeasureCalculator.calcule(graph);
        out.printLog("Global measures: " + global.toString());
//        Map<String, Double> barycenter = BarycenterCalculator.calcule(graph, edgesWeigth);
        Map<String, Double> betweenness = BetweennessCalculator.calcule(graph, edgesWeigth);
        Map<String, Double> closeness = ClosenessCalculator.calcule(graph, edgesWeigth);
        Map<String, Integer> degree = DegreeCalculator.calcule(graph);
        Map<String, Double> eigenvector = EigenvectorCalculator.calcule(graph, edgesWeigth);
        Map<String, EgoMeasure<String>> ego = EgoMeasureCalculator.calcule(graph, edgesWeigth);
        Map<String, StructuralHolesMeasure<String>> structuralHoles = StructuralHolesCalculator.calcule(graph, edgesWeigth);

        // number of pull requests in date interval
        Long numberOfAllPullrequestFuture = pairFileDAO.calculeNumberOfPullRequest(getRepository(), null, null, futureBeginDate, futureEndDate, true);
        // cache for optimization number of pull requests where file is in,
        // reducing access to database
        Map<String, Long> pullRequestFileMap = new HashMap<>();
        // cache for optimization file code churn (add, del, change),
        // reducing access to database
        Map<String, AuxCodeChurn> codeChurnRequestFileMap = new HashMap<>();
        Map<String, AuxCodeChurn> cummulativeCodeChurnRequestFileMap = new HashMap<>();
        // cache for optimization file commits made by user,
        // reducing access to database
        Map<String, AuxCodeChurn> fileUserCommitMap = new HashMap<>();

        out.printLog("Calculando somas, máximas, médias, updates, code churn e apriori para cada par de arquivos...");
        count = 0;
        final int size = commitersPairFile.entrySet().size();
        out.printLog("Número de pares de arquivos: " + commitersPairFile.keySet().size());
        for (Map.Entry<AuxFileFile, Set<String>> entry : commitersPairFile.entrySet()) {
            if (count++ % 100 == 0 || count == size) {
                System.out.println(count + "/" + size);
            }
            AuxFileFile fileFile = entry.getKey();
            Set<String> devsCommentters = entry.getValue();
            
            // pair file network
            GlobalMeasure pairFileGlobal = GlobalMeasureCalculator.calcule(pairFileNetwork.get(fileFile));
            
//            Double barycenterSum = 0d, barycenterAvg, barycenterMax = Double.NEGATIVE_INFINITY;
            Double betweennessSum = 0d, betweennessAvg, betweennessMax = Double.NEGATIVE_INFINITY;
            Double closenessSum = 0d, closenessAvg, closenessMax = Double.NEGATIVE_INFINITY;
            Integer degreeSum = 0, degreeMax = Integer.MIN_VALUE; 
            Double degreeAvg;
            Double eigenvectorSum = 0d, eigenvectorAvg, eigenvectorMax = Double.NEGATIVE_INFINITY;

            Double egoBetweennessSum = 0d, egoBetweennessAvg, egoBetweennessMax = Double.NEGATIVE_INFINITY;
            Long egoSizeSum = 0l, egoSizeMax = Long.MIN_VALUE;
//            Long egoPairsSum = 0l, egoPairsMax = Long.MIN_VALUE;
            Long egoTiesSum = 0l, egoTiesMax = Long.MIN_VALUE;
            Double egoSizeAvg, /*egoPairsAvg,*/ egoTiesAvg;
            Double egoDensitySum = 0d, egoDensityAvg, egoDensityMax = Double.NEGATIVE_INFINITY;

            Double efficiencySum = 0.0d, efficiencyAvg, efficiencyMax = Double.NEGATIVE_INFINITY;
            Double effectiveSizeSum = 0.0d, effectiveSizeAvg, effectiveSizeMax = Double.NEGATIVE_INFINITY;
            Double constraintSum = 0.0d, constraintAvg, constraintMax = Double.NEGATIVE_INFINITY;
            Double hierarchySum = 0.0d, hierarchyAvg, hierarchyMax = Double.NEGATIVE_INFINITY;

            for (String devCommentter : devsCommentters) {
                // sums calculation
//                barycenterSum += barycenter.get(devCommentter);
                betweennessSum += betweenness.get(devCommentter);
                closenessSum += Double.isInfinite(closeness.get(devCommentter)) ? 0 : closeness.get(devCommentter);
                degreeSum += degree.get(devCommentter);
                eigenvectorSum += eigenvector.get(devCommentter);

                egoBetweennessSum += ego.get(devCommentter).getBetweennessCentrality();
                egoSizeSum += ego.get(devCommentter).getSize();
//                egoPairsSum += ego.get(devCommentter).getPairs();
                egoTiesSum += ego.get(devCommentter).getTies();
                egoDensitySum += ego.get(devCommentter).getDensity();

                efficiencySum += structuralHoles.get(devCommentter).getEfficiency();
                effectiveSizeSum += structuralHoles.get(devCommentter).getEffectiveSize();
                constraintSum += structuralHoles.get(devCommentter).getConstraint();
                hierarchySum += structuralHoles.get(devCommentter).getHierarchy();
                // maximum calculation
//                barycenterMax = Math.max(barycenterMax, barycenter.get(devCommentter));
                betweennessMax = Math.max(betweennessMax, betweenness.get(devCommentter));
                closenessMax = Math.max(closenessMax, Double.isInfinite(closeness.get(devCommentter)) ? 0 : closeness.get(devCommentter));
                degreeMax = Math.max(degreeMax, degree.get(devCommentter));
                eigenvectorMax = Math.max(eigenvectorMax, eigenvector.get(devCommentter));

                egoBetweennessMax = Math.max(egoBetweennessMax, ego.get(devCommentter).getBetweennessCentrality());
                egoSizeMax = Math.max(egoSizeMax, ego.get(devCommentter).getSize());
//                egoPairsMax = Math.max(egoPairsMax, ego.get(devCommentter).getPairs());
                egoTiesMax = Math.max(egoTiesMax, ego.get(devCommentter).getTies());
                egoDensityMax = Math.max(egoDensityMax, ego.get(devCommentter).getDensity());

                efficiencyMax = Math.max(efficiencyMax, structuralHoles.get(devCommentter).getEfficiency());
                effectiveSizeMax = Math.max(effectiveSizeMax, structuralHoles.get(devCommentter).getEffectiveSize());
                constraintMax = Math.max(constraintMax, structuralHoles.get(devCommentter).getConstraint());
                hierarchyMax = Math.max(hierarchyMax, structuralHoles.get(devCommentter).getHierarchy());

            }

            // Average calculation /////////////////////////////////////////////
            double distinctCommentersCount = devsCommentters.size();
//            barycenterAvg = barycenterSum / (double) distinctCommentersCount;
            betweennessAvg = betweennessSum / distinctCommentersCount;
            closenessAvg = closenessSum / distinctCommentersCount;
            degreeAvg = degreeSum / distinctCommentersCount;
            eigenvectorAvg = eigenvectorSum / distinctCommentersCount;

            egoBetweennessAvg = egoBetweennessSum / distinctCommentersCount;
            egoSizeAvg = egoSizeSum / distinctCommentersCount;
//            egoPairsAvg = egoPairsSum / distinctCommentersCount;
            egoTiesAvg = egoTiesSum / distinctCommentersCount;
            egoDensityAvg = egoDensitySum / distinctCommentersCount;

            efficiencyAvg = efficiencySum / distinctCommentersCount;
            effectiveSizeAvg = effectiveSizeSum / distinctCommentersCount;
            constraintAvg = constraintSum / distinctCommentersCount;
            hierarchyAvg = hierarchySum / distinctCommentersCount;

            // Commit-based metrics ////////////////////////////////////////////
            final long changes = calculeFileCodeChurn(codeChurnRequestFileMap, fileFile.getFileName(), fileDAO, beginDate, endDate);
            final long changes2 = calculeFileCodeChurn(codeChurnRequestFileMap, fileFile.getFileName2(), fileDAO, beginDate, endDate);

            final long cummulativeChanges = calculeFileCodeChurn(cummulativeCodeChurnRequestFileMap, fileFile.getFileName(), fileDAO, null, endDate);
            final long cummulativeChanges2 = calculeFileCodeChurn(cummulativeCodeChurnRequestFileMap, fileFile.getFileName2(), fileDAO, null, endDate);

            Set<AuxUser> devsCommitters = pairFileDAO.selectCommitters(repository,
                    fileFile.getFileName(), fileFile.getFileName2(), beginDate, endDate);

            Long devCommitsSum = 0l, devCommitsMax = Long.MIN_VALUE;
            Double devCommitsAvg;
            Double ownershipSum = 0.0d, ownershipAvg, ownershipMax = Double.NEGATIVE_INFINITY;
            Long minorContributors = 0l, majorContributors = 0l;
            Double ownerExperience = 0.0d, ownerExperience2 = 0.0d, cummulativeOwnerExperience = 0.0d, cummulativeOwnerExperience2 = 0.0d;

            long committers = devsCommitters.size();
            long distinctCommitters = pairFileDAO.calculeCommitters(repository,
                    fileFile.getFileName(), fileFile.getFileName2(), null, endDate);

            Long commits = pairFileDAO.calculeCommits(repository,
                    fileFile.getFileName(), fileFile.getFileName2(),
                    beginDate, endDate);

            for (AuxUser devCommitter : devsCommitters) {
                Long devCommits = pairFileDAO.calculeCommits(repository,
                        fileFile.getFileName(), fileFile.getFileName2(), devCommitter.getUser(),
                        beginDate, endDate);
                devCommitsSum += devCommits;

                Double ownership = devCommits.doubleValue() / commits.doubleValue();
                ownershipSum += ownership;

                if (ownership <= 0.05) { // menor ou igual que 5% = minor
                    minorContributors++;
                } else { // maior que 5% = major
                    majorContributors++;
                }

                devCommitsMax = Math.max(devCommitsMax, devCommits);
                ownershipMax = Math.max(ownershipMax, ownership);

                // Calculing OEXP of each file
                double experience = calculeDevFileExperience(changes, fileUserCommitMap, fileFile.getFileName(), devCommitter.getUser(), fileDAO, beginDate, endDate);
                ownerExperience = Math.max(experience, ownerExperience);

                double experience2 = calculeDevFileExperience(changes2, fileUserCommitMap, fileFile.getFileName2(), devCommitter.getUser(), fileDAO, beginDate, endDate);
                ownerExperience2 = Math.max(experience2, ownerExperience2);

                // Calculing OWN
                double cumulativeExperience = calculeDevFileExperience(cummulativeChanges, fileUserCommitMap, fileFile.getFileName(), devCommitter.getUser(), fileDAO, null, endDate);
                cummulativeOwnerExperience = Math.max(cummulativeOwnerExperience, cumulativeExperience);

                double cumulativeExperience2 = calculeDevFileExperience(cummulativeChanges2, fileUserCommitMap, fileFile.getFileName2(), devCommitter.getUser(), fileDAO, null, endDate);
                cummulativeOwnerExperience2 = Math.max(cummulativeOwnerExperience2, cumulativeExperience2);

            }

            devCommitsAvg = (double) devCommitsSum / (double) committers;
            ownershipAvg = ownershipSum / committers;

            Long updates = pairFileDAO.calculeNumberOfPullRequest(repository,
                    fileFile.getFileName(), fileFile.getFileName2(), 
                    beginDate, endDate, true);

            Long futureUpdates;
            if (beginDate.equals(futureBeginDate) && endDate.equals(futureEndDate)) {
                futureUpdates = updates;
            } else {
                futureUpdates = pairFileDAO.calculeNumberOfPullRequest(repository,
                        fileFile.getFileName(), fileFile.getFileName2(),
                        futureBeginDate, futureEndDate, true);
            }

            Long commentsSum = pairFileDAO.calculeComments(repository,
                    fileFile.getFileName(), fileFile.getFileName2(), 
                    beginDate, endDate, true);

            Long codeChurn = pairFileDAO.calculeCodeChurn(repository,
                    fileFile.getFileName(), fileFile.getFileName2(), 
                    beginDate, endDate);
            Long codeChurn2 = pairFileDAO.calculeCodeChurn(repository,
                    fileFile.getFileName2(), fileFile.getFileName(),
                    beginDate, endDate);

            AuxCodeChurn pairFileCodeChurn = pairFileDAO.calculeCodeChurnAddDelChange(repository,
                    fileFile.getFileName2(), fileFile.getFileName(),
                    beginDate, endDate);

            double codeChurnAvg = (codeChurn + codeChurn2) / 2.0d;

            AuxFileFileMetrics auxFileFileMetrics = new AuxFileFileMetrics(
                    fileFile.getFileName(), fileFile.getFileName2(), 
//                        barycenterSum, barycenterAvg, barycenterMax,
                    betweennessSum, betweennessAvg, betweennessMax,
                    closenessSum, closenessAvg, closenessMax,
                    degreeSum, degreeAvg, degreeMax,
                    eigenvectorSum, eigenvectorAvg, eigenvectorMax,

                    egoBetweennessSum, egoBetweennessAvg, egoBetweennessMax,
                    egoSizeSum, egoSizeAvg, egoSizeMax,
                    egoTiesSum, egoTiesAvg, egoTiesMax,
//                    egoPairsSum, egoPairsAvg, egoPairsMax,
                    egoDensitySum, egoDensityAvg, egoDensityMax,

                    efficiencySum, efficiencyAvg, efficiencyMax, 
                    effectiveSizeSum, effectiveSizeAvg, effectiveSizeMax,
                    constraintSum, constraintAvg, constraintMax,
                    hierarchySum, hierarchyAvg, hierarchyMax,

                    pairFileGlobal.getSize(), pairFileGlobal.getTies(),
                    pairFileGlobal.getDensity(), pairFileGlobal.getDiameter(), 
                    devCommitsSum, devCommitsAvg, devCommitsMax,
                    ownershipSum, ownershipAvg, ownershipMax,
                    majorContributors, minorContributors,
                    ownerExperience, ownerExperience2,
                    cummulativeOwnerExperience, cummulativeOwnerExperience2,
                    committers, distinctCommitters, commits,
                    distinctCommentersCount, commentsSum,
                    codeChurn, codeChurn2, codeChurnAvg,
                    pairFileCodeChurn.getAdditionsNormalized(), pairFileCodeChurn.getDeletionsNormalized(), pairFileCodeChurn.getChanges(),
                    updates, futureUpdates
            );

            // apriori
//            // same as updates
//            Long pairFileNumberOfPullrequestOfPair = pairFileDAO.calculeNumberOfPullRequest(getRepository(), auxFileFileMetrics.getFile(), auxFileFileMetrics.getFile2(), getBeginDate(), getEndDate(), true);
//            // same as futureUpdates
//            Long pairFileNumberOfPullrequestOfPairFuture = pairFileDAO.calculeNumberOfPullRequest(getRepository(), auxFileFileMetrics.getFile(), auxFileFileMetrics.getFile2(), futureBeginDate, futureEndDate, true);
            Long fileNumberOfPullrequestOfPairFuture;
            if (pullRequestFileMap.containsKey(auxFileFileMetrics.getFile())) {
                fileNumberOfPullrequestOfPairFuture = pullRequestFileMap.get(auxFileFileMetrics.getFile());
            } else {
                fileNumberOfPullrequestOfPairFuture = pairFileDAO.calculeNumberOfPullRequest(getRepository(), auxFileFileMetrics.getFile(), null, futureBeginDate, futureEndDate, true);
                pullRequestFileMap.put(auxFileFileMetrics.getFile(), fileNumberOfPullrequestOfPairFuture);
            }

            Long file2NumberOfPullrequestOfPairFuture;
            if (pullRequestFileMap.containsKey(auxFileFileMetrics.getFile2())) {
                file2NumberOfPullrequestOfPairFuture = pullRequestFileMap.get(auxFileFileMetrics.getFile2());
            } else {
                file2NumberOfPullrequestOfPairFuture = pairFileDAO.calculeNumberOfPullRequest(getRepository(), auxFileFileMetrics.getFile2(), null, futureBeginDate, futureEndDate, true);
                pullRequestFileMap.put(auxFileFileMetrics.getFile2(), file2NumberOfPullrequestOfPairFuture);
            }

            auxFileFileMetrics.addMetrics(updates, futureUpdates, fileNumberOfPullrequestOfPairFuture, file2NumberOfPullrequestOfPairFuture, numberOfAllPullrequestFuture);

            Double supportFile = fileNumberOfPullrequestOfPairFuture.doubleValue() / numberOfAllPullrequestFuture.doubleValue();
            Double supportFile2 = file2NumberOfPullrequestOfPairFuture.doubleValue() / numberOfAllPullrequestFuture.doubleValue();
            Double supportPairFile = futureUpdates.doubleValue() / numberOfAllPullrequestFuture.doubleValue();
            Double confidence = supportFile == 0 ? 0d : supportPairFile / supportFile;
            Double confidence2 = supportFile2 == 0 ? 0d : supportPairFile / supportFile2;
            Double lift = supportFile * supportFile2 == 0 ? 0d : supportPairFile / (supportFile * supportFile2);
            Double conviction = 1 - confidence == 0 ? 0d : (1 - supportFile) / (1 - confidence);
            Double conviction2 = 1 - confidence2 == 0 ? 0d : (1 - supportFile2) / (1 - confidence2);

            auxFileFileMetrics.addMetrics(supportFile, supportFile2, supportPairFile, confidence, confidence2, lift, conviction, conviction2);

            fileFileMetrics.add(auxFileFileMetrics);
        }

        out.printLog("Número de pares de arquivos: " + fileFileMetrics.size());
        addToEntityMetricNodeList(fileFileMetrics);
    }

    public long calculeFileCodeChurn(Map<String, AuxCodeChurn> codeChurnRequestFileMap, String fileName, FileDAO fileDAO, Date beginDate, Date endDate) {
        final long /*additions, deletions,*/ changes;
        if (codeChurnRequestFileMap.containsKey(fileName)) { // cached
            AuxCodeChurn sumCodeChurnFile = codeChurnRequestFileMap.get(fileName);
//                additions = sumCodeChurnFile.getAdditions();
//                deletions = sumCodeChurnFile.getDeletions();
            changes = sumCodeChurnFile.getChanges();
        } else {
            AuxCodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(repository, fileName, beginDate, endDate);
            codeChurnRequestFileMap.put(fileName, sumCodeChurnFile);
//                additions = sumCodeChurnFile.getAdditions();
//                deletions = sumCodeChurnFile.getDeletions();
            changes = sumCodeChurnFile.getChanges();
        }
        return changes;
    }

    public double calculeDevFileExperience(final Long changes, Map<String, AuxCodeChurn> fileUserCommitMap, String fileName, String user, FileDAO fileDAO, Date beginDate, Date endDate) {
        final long /*devAdditions, devDeletions,*/ devChanges;
        if (fileUserCommitMap.containsKey(fileName)) { // cached
            AuxCodeChurn sumCodeChurnFile = fileUserCommitMap.get(fileName);
//                    devAdditions = sumCodeChurnFile.getAdditions();
//                    devDeletions = sumCodeChurnFile.getDeletions();
            devChanges = sumCodeChurnFile.getChanges();
        } else {
            AuxCodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(repository,
                    fileName, user, beginDate, endDate
            );
            fileUserCommitMap.put(fileName, sumCodeChurnFile);
//                    devAdditions = sumCodeChurnFile.getAdditions();
//                    devDeletions = sumCodeChurnFile.getDeletions();
            devChanges = sumCodeChurnFile.getChanges();
        }

        return changes == 0 ? 0.0d : (double) devChanges / (double) changes;
    }

    @Override
    public String getHeadCSV() {
        return "file;file2;"
                // + "brcAvg;brcSum;brcMax;"
                + "btwSum;btwAvg;btwMax;"
                + "clsSum;clsAvg;clsMax;"
                + "dgrSum;dgrAvg;dgrMax;"
                + "egvSum;egvAvg;egvMax;"
                + "egoBtwSum;egoBtwAvg;egoBtwMax;"
                + "egoSizeSum;egoSizeAvg;egoSizeMax;"
                + "egoTiesSum;egoTiesAvg;egoTiesMax;"
                // + "egoPairsSum;egoPairsAvg;egoPairsMax;"
                + "egoDensitySum;egoDensityAvg;egoDensityMax;"
                + "efficiencySum;efficiencyAvg;efficiencyMax;"
                + "efvSizeSum;efvSizeAvg;efvSizeMax;"
                + "constraintSum;constraintAvg;constraintMax;"
                + "hierarchySum;hierarchyAvg;hierarchyMax;"
                + "size;ties;density;diameter;"
                + "devCommitsSum;devCommitsAvg;devCommitsMax;"
                + "ownershipSum;ownershipAvg;ownershipMax;"
                + "majorContributors;minorContributors;"
                + "oexp;oexp2;"
                + "own;own2;"
                + "adev;ddev;commits;"
                + "commenters;comments;"
                + "codeChurn;codeChurn2;codeChurnAvg;"
                + "add;del;changes;"
                + "updates;futureUpdates;"
                + "pairFileCochange;pairFileCochangeFuture;fileChangeFuture;file2ChangeFuture;allPullrequestFuture;"
                + "supportFile;supportFile2;supportPairFile;confidence;confidence2;lift;conviction;conviction2";
    }

    @Override
    public List<String> getAvailableMatricesPermitted() {
        return Arrays.asList(UserCommentedSamePairOfFileInDateServices.class.getName(),
                UserCommentedSamePairOfFileInNumberServices.class.getName());
    }

    private EntityRepository getRepository() {
        String[] repoStr = getMatrix().getRepository().split("/");
        List<EntityRepository> repos = dao.executeNamedQueryWithParams(
                "Repository.findByNameAndOwner",
                new String[]{"login", "name"},
                new Object[]{repoStr[0], repoStr[1]});
        if (repos.size() == 1) {
            return repos.get(0);
        }
        return null;
    }

    private boolean isIgnored(String fileName) {
        return fileName.contains("README")
                || fileName.endsWith("Rakefile")
                || fileName.contains("CHANGELOG")
                || fileName.contains("Gemfile")
                || fileName.endsWith(".gitignore");
    }
}