import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.reindahl.audioplayer.library.Library;


public class LibraryTest {

	@Test
	public void arrayListDifferenceTest() {
		ArrayList<String> a=new ArrayList<>();
		a.add("3");
		a.add("1");
		a.add("2");
		ArrayList<String> b=new ArrayList<>();		
		b.add("3");
		b.add("4");
		b.add("2");
		b.add("sadd");
		ArrayList<String> difA=new ArrayList<>();
		ArrayList<String> difB=new ArrayList<>();
		
		
		ArrayList<String> controlA=new ArrayList<>(a);
		ArrayList<String> controlB=new ArrayList<>(b);
		
		Library.arrayListDifference(a, b, difA, difB);
		
		assertEquals(1, difA.size());
		assertEquals("1",difA.get(0));
		
		assertEquals(2, difB.size());
		assertEquals("4",difB.get(0));
		assertEquals("sadd",difB.get(1));
		
		assertEquals(controlA.size(), a.size());
		assertTrue(a.containsAll(controlA));
		
		assertEquals(controlB.size(), b.size());
		assertTrue(b.containsAll(controlB));


	}

	@Test
	public void arrayListDifferenceTest2() {
		ArrayList<String> a=new ArrayList<>();
		ArrayList<String> b=new ArrayList<>();		
		b.add("3");
		b.add("4");
		b.add("2");
		b.add("sadd");
		ArrayList<String> difA=new ArrayList<>();
		ArrayList<String> difB=new ArrayList<>();
		
		
		
		ArrayList<String> controlB=new ArrayList<>(b);
		
		Library.arrayListDifference(a, b, difA, difB);
		
		assertEquals(0, difA.size());
		System.out.println(difB);
		assertEquals(4, difB.size());
		assertEquals("2",difB.get(0));
		assertEquals("3",difB.get(1));
		assertEquals("4",difB.get(2));
		assertEquals("sadd",difB.get(3));

		
		assertEquals(controlB.size(), b.size());
		assertTrue(b.containsAll(controlB));

		
		
		a.add("3");
		a.add("1");
		a.add("2");
		b.clear();
		difA.clear();
		difB.clear();
		ArrayList<String> controlA=new ArrayList<>(a);
		Library.arrayListDifference(a, b, difA, difB);
		
		assertEquals(0, difB.size());
		
		assertEquals(3, difA.size());
		assertEquals("1",difA.get(0));
		assertEquals("2",difA.get(1));
		assertEquals("3",difA.get(2));

		
		assertEquals(controlA.size(), a.size());
		assertTrue(a.containsAll(controlA));

		
	}
		
}
