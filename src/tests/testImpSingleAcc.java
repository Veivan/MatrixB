package tests;

import org.junit.Before;
import org.junit.Test;

import service.ImpSingleAcc;

public class testImpSingleAcc {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		int group_id = 6;
		String data = "oN8PF9GftQAVxt9:ehpK1IJ16J:leonidkol26@list.ru:kystxR2m";
		ImpSingleAcc ipm = new ImpSingleAcc(data, ImpSingleAcc.cDatatype.NPEM, group_id);
		ipm.run();
	}

}
