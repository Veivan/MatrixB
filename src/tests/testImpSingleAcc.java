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
		String data = "KfqBh03yWAVA3aw:31QRmdjjQd:egor0wln@mail.ru:zTytr6QrE";
		ImpSingleAcc ipm = new ImpSingleAcc(data, ImpSingleAcc.cDatatype.NPEM, group_id);
		ipm.run();
	}

}
