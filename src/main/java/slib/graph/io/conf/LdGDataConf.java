package slib.graph.io.conf;

import java.io.FileInputStream;

import slib.graph.io.util.GFormat;

public class LdGDataConf extends GDataConf {
	
	private FileInputStream in =null;

	public LdGDataConf(GFormat format, FileInputStream in) {
		super(format);
		this.in = in;
	}

}
