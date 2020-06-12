package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.WhereClauseParser;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

public class WhereClauseParserTests {

	@Test
	public void testSpecialChars() throws ParseException {
		String input = "a.eq.";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{STRING=}}}", ast.format());

		we.setInput("a.eq.null");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{NULL}}}", ast.format());

		we.setInput("a.eq.1\\.and\\(33\\)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{STRING=1.and(33)}}}", ast.format());

		we.setInput("a.eq.1\\.2");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=eq}{NUMBER=1.2}}}", ast.format());

		we.setInput("a.bbi.(1,2.3,7.0000)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=bbi}LIST{{NUMBER=1}{NUMBER=2.3}{NUMBER=7.0000}}}}", ast.format());

		we.setInput("a.ire.\\.*\\,3");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=ire}{STRING=.*,3}}}", ast.format());

		we.setInput("scode.ire.\\(TRENTO|rovereto\\)\\.*,mvalue.neq.0");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=scode}{OP=ire}{STRING=(TRENTO|rovereto).*}}CLAUSE{{ALIAS=mvalue}{OP=neq}{NUMBER=0}}}", ast.format());

		we.setInput("a_b.eq.ABC");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a_b}{OP=eq}{STRING=ABC}}}", ast.format());
	}

	@Test
	public void testLists() throws ParseException {
		String input = "a.in.()";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=in}LIST{{STRING=}}}}", ast.format());

		we.setInput("a.in.(null)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=in}LIST{{NULL}}}}", ast.format());

		we.setInput("a.bbi.(1,2,3,4,5)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{OP=bbi}LIST{{NUMBER=1}{NUMBER=2}{NUMBER=3}{NUMBER=4}{NUMBER=5}}}}", ast.format());

		we.setInput("scode.ire.(TRENTO|rovereto)");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=scode}{OP=ire}LIST{{STRING=TRENTO|rovereto}}}}", ast.format());

		we.setInput("scode.ire.info@example\\.com");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=scode}{OP=ire}{STRING=info@example.com}}}", ast.format());

		we.setInput("scode.ire.(TRENTO|rovereto).*,mvalue.neq.0");
		try {
			ast = we.parse();
			fail("Exception expected; Syntax error at . after rovereto).... ire.<LIST> will be checked later, not during parser stage.");
		} catch (SimpleException e) {
			assertEquals("PARSING ERROR: Syntax error at position 27 with character .: One of the following characters ,)<EOL> expected", e.getMessage());
		}

		we.setInput("scode.ire.(TRENTO|rovereto).*,mvalue.neq.0");
		try {
			ast = we.parse();
			fail("Exception expected; Syntax error at . after rovereto).... ire.<LIST> will be checked later, not during parser stage.");
		} catch (SimpleException e) {
			assertEquals("PARSING ERROR: Syntax error at position 27 with character .: One of the following characters ,)<EOL> expected", e.getMessage());
		}

		we.setInput("a.eq.1.and(a.eq.0)");
		try {
			ast = we.parse();
			fail("Exception expected; Syntax error at ( after and");
		} catch (SimpleException e) {
			assertEquals("PARSING ERROR: Syntax error at position 4 with character .: OPERATOR expected", e.getMessage());
		}

		we.setInput("or(scode.ire.TRENTO|rovere'to.*,mvalue.eq.0)");
		try {
			ast = we.parse();
			fail("Exception expected; Syntax error at ' after rovere");
		} catch (SimpleException e) {
			assertEquals("PARSING ERROR: Syntax error at position 26 with character ': Characters ('\" must be escaped within a filter VALUE", e.getMessage());
		}
	}

	@Test
	public void testJson() throws ParseException {
		String input = "a.b.c.in.()";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=a}{JSONSEL=b.c}{OP=in}LIST{{STRING=}}}}", ast.format());

		we.setInput("smetadata.outlets.0.maxPower.gt.3.7");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.maxPower}{OP=gt}{NUMBER=3.7}}}", ast.format());

		we.setInput("smetadata.building_code.eq.A4");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=building_code}{OP=eq}{STRING=A4}}}", ast.format());

		we.setInput("smetadata.outlets.0.maxPower.gt.-3.7");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.maxPower}{OP=gt}{NUMBER=-3.7}}}", ast.format());

		we.setInput("smetadata.outlets.0.maxPower.gt.-.7");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.maxPower}{OP=gt}{NUMBER=-.7}}}", ast.format());

		we.setInput("smetadata.outlets.0.maxPower.gt.-2.");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.maxPower}{OP=gt}{NUMBER=-2.}}}", ast.format());

		we.setInput("smetadata.outlets.0.maxPower.gt.+2");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.maxPower}{OP=gt}{NUMBER=+2}}}", ast.format());

		we.setInput("smetadata.outlets.0.type.ire.\"what.*ever\"");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.type}{OP=ire}{STRING=what.*ever}}}", ast.format());

		we.setInput("smetadata.outlets.0.type.ire.what.*ever");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.type.ire}{OP=what}{STRING=*ever}}}", ast.format());

		we.setInput("smetadata.outlets.0.type.ire.what\\.*ever");
		ast = we.parse();
		assertEquals("AND{CLAUSE{{ALIAS=smetadata}{JSONSEL=outlets.0.type}{OP=ire}{STRING=what.*ever}}}", ast.format());
	}


}
