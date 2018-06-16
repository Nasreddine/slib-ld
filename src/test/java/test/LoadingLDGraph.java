/* 
 *  Copyright or © or Copr. Ecole des Mines d'Alès (2012-2014) 
 *  
 *  This software is a computer program whose purpose is to provide 
 *  several functionalities for the processing of semantic data 
 *  sources such as ontologies or text corpora.
 *  
 *  This software is governed by the CeCILL  license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL
 *  "http://www.cecill.info". 
 * 
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 

 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 * 
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms.
 */
package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.ParameterizedSparqlString;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.vocabulary.RDFS;

import org.openrdf.model.URI;

import sc.research.ldq.LdDataset;
import sc.research.ldq.LdDatasetFactory;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.conf.GraphConf;
import slib.graph.io.conf.LdGDataConf;
import slib.graph.io.loader.ld.GraphLoaderGeneric;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;

import slib.utils.ex.SLIB_Exception;
import slib.utils.impl.Timer;

/**
 *
 * @author Sébastien Harispe (sebastien.harispe@gmail.com)
 */
public class LoadingLDGraph {

    public static void main(String[] args) throws SLIB_Exception {

        Timer t = new Timer();
        t.start();
        
        
        
        
        
        PrefixMapping prefixes = new PrefixMappingImpl();

		prefixes.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");
		prefixes.setNsPrefix("dbpedia-fr", "http://fr.dbpedia.org/resource/");
		prefixes.setNsPrefix("dbpedia-owl", "http://dbpedia.org/ontology/");

		LdDataset fr_DBpedia_dataset = null;
		try {
			fr_DBpedia_dataset = LdDatasetFactory.getInstance()
								 .link("http://fr.dbpedia.org/sparql")
								 .name("fr-dbpedia")
								 .prefixes(prefixes)
								 .create();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
        
        
		ParameterizedSparqlString query_cmd = fr_DBpedia_dataset.prepareQuery();

		String match_label = "Château de Cheverny";

		query_cmd.setCommandText("select * where {" 
		   + "?resource <"+ RDFS.label + "> ?label. " + "}");

		query_cmd.setLiteral("label", match_label, "fr");

		//logger.info("query = " + query_cmd.toString());

		ResultSet resultSet = fr_DBpedia_dataset.executeSelectQuery(query_cmd.toString());

//		List<String> mapped_resources = new ArrayList<String>();
//
//		while (resultSet.hasNext()) {
//			QuerySolution qs = resultSet.
//			mapped_resources.add(qs.toString());
//		}
		
		Model m = resultSet.getResourceModel();
        
		FileOutputStream out = null;
		FileInputStream in = null;
		
        m.write(out);
        
        //IOUtils.copy(out, in);

        
        

        URIFactory factory = URIFactoryMemory.getSingleton();
        URI graphURI = factory.getURI("http://graph/");
        G g = new GraphMemory(graphURI);

        GDataConf dataConf = new LdGDataConf(GFormat.NTRIPLES, in);
        
        

        // We specify an action to root the vertices, typed as class without outgoing rdfs:subclassOf relationship 
        // Those vertices are linked to owl:Thing by an eddge x  rdfs:subClassOf owl:Thing 
       // GAction actionRerootConf = new GAction(GActionType.REROOTING);

        // We now create the configuration we will specify to the generic loader
        GraphConf gConf = new GraphConf();
        gConf.addGDataConf(dataConf);
      //  gConf.addGAction(actionRerootConf);

        GraphLoaderGeneric.load(gConf, g);

        showVerticesAndEdges(g);

    //    Set<URI> roots = new ValidatorDAG().getTaxonomicRoots(g);
    //    System.out.println("Roots: " + roots);

/*
        // We compute the similarity between two concepts 
        URI countryURI = factory.getURI("https://sites.google.com/site/portdial2/downloads-area/Travel-Domain.owl#Country");
        URI cityURI = factory.getURI("https://sites.google.com/site/portdial2/downloads-area/Travel-Domain.owl#City");

        // First we configure an intrincic IC 
        ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_DEPTH_MAX_NONLINEAR);
        // Then we configure the pairwise measure to use, we here choose to use Lin formula
        SMconf smConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);

        // We define the engine used to compute the similarity
        SM_Engine engine = new SM_Engine(g);

        double sim = engine.compare(smConf, countryURI, cityURI);
        System.out.println("Similarity: " + sim);

        /* 
         * Notice that the first computation is expensive as the engine compute the IC and extra information 
         * which are cached by the engine
         * Let's perform numerous random computations (we only print some results).
         * We retrieve the set of vertices as a list
         */

        t.stop();
        t.elapsedTime();
    }
    
  private static void showVerticesAndEdges(G graph) {
        
        
        Set<URI> vertices = graph.getV();
        Set<E> edges = graph.getE();
        
        System.out.println("-Vertices");
        for(URI v : vertices){
            System.out.println("\t"+v);
        }
        
        System.out.println("-Edge");
        for(E edge : edges){
            System.out.println("\t"+edge);
        }
    }
}
