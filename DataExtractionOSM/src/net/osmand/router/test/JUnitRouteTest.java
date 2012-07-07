package net.osmand.router.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.router.BinaryRoutePlanner;
import net.osmand.router.RoutingConfiguration;
import net.osmand.swing.DataExtractionSettings;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;


public class JUnitRouteTest  {

	static BinaryMapIndexReader[]  rs;
	@Before
	public void setupFiles() throws IOException {
		if(rs != null){
			return;
		}
		BinaryRoutePlanner.PRINT_TO_CONSOLE_ROUTE_INFORMATION_TO_TEST = false;
		
		List<File> files = new ArrayList<File>();  
		for (File f : new File(DataExtractionSettings.getSettings().getBinaryFilesDir()).listFiles()) {
			if (f.getName().endsWith(".obf")) {
				files.add(f);
			}
		}
		rs = new BinaryMapIndexReader[files.size()];
		int it = 0;
		for (File f : files) {
			RandomAccessFile raf = new RandomAccessFile(f, "r"); //$NON-NLS-1$ //$NON-NLS-2$
			rs[it++] = new BinaryMapIndexReader(raf, false);
		}
	}
		
	
	@Test
	public void runNL() throws SAXException, IOException, ParserConfigurationException {
		RouterTestsSuite.test(getClass().getResourceAsStream("nl.test.xml"), rs, RoutingConfiguration.getDefault());
	}

	@Test
	public void runNL2() throws SAXException, IOException, ParserConfigurationException {
		RouterTestsSuite.test(getClass().getResourceAsStream("nl2.test.xml"), rs, RoutingConfiguration.getDefault());
	}
	
}
