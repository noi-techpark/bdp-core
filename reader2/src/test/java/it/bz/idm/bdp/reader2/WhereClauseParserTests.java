package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.miniparser.Token;
import it.bz.idm.bdp.reader2.utils.querybuilder.WhereClauseParser;
import it.bz.idm.bdp.reader2.utils.simpleexception.SimpleException;

public class WhereClauseParserTests {

	@Test
	public void testSpecialChars() throws ParseException {
		String input = "a.eq.";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{VALUE=}}}", ast.format());

		we.setInput("a.eq.null");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{NULL}}}", ast.format());

		we.setInput("a.eq.1.and\\(33\\)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{VALUE=1.and(33)}}}", ast.format());

		we.setInput("a.eq.1.2");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{VALUE=1.2}}}", ast.format());

		we.setInput("a.bbi.(1,2.3,7.0000)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=bbi}LIST{{VALUE=1}{VALUE=2.3}{VALUE=7.0000}}}}", ast.format());

		we.setInput("a.ire..*\\,3");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=ire}{VALUE=.*,3}}}", ast.format());

		we.setInput("scode.ire.\\(TRENTO|rovereto\\).*,mvalue.neq.0");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=scode}{OP=ire}{VALUE=(TRENTO|rovereto).*}}CLAUSE{{ALIAS=mvalue}{OP=neq}{VALUE=0}}}", ast.format());
	}

	@Test
	public void testLists() throws ParseException {
		String input = "a.in.()";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=in}LIST{{VALUE=}}}}", ast.format());

		we.setInput("a.in.(null)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=in}LIST{{NULL}}}}", ast.format());

		we.setInput("a.bbi.(1,2,3,4,5)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=bbi}LIST{{VALUE=1}{VALUE=2}{VALUE=3}{VALUE=4}{VALUE=5}}}}", ast.format());

		we.setInput("scode.ire.(TRENTO|rovereto).*,mvalue.neq.0");
		try {
			ast = we.parse();
		} catch (SimpleException e) {

		}
		System.out.println(ast.prettyFormat());
		System.out.println(ast.format());
		assertEquals("AND{CLAUSE{{ALIAS=scode}{OP=ire}{VALUE=(TRENTO|rovereto).*}}CLAUSE{{ALIAS=mvalue}{OP=neq}{VALUE=0}}}", ast.format());
	}


}
