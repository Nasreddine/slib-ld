package slib.graph.io.loader.ld;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.util.RDFLoader;
import org.openrdf.rio.ParserConfig;

import slib.graph.model.graph.G;
import slib.utils.ex.SLIB_Ex_Critic;

public class LdLoader extends RDFLoader {

	public LdLoader(ParserConfig config, ValueFactory vf) {
		super(config, vf);
		// TODO Auto-generated constructor stub
	}

	
}
