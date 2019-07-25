package it.bz.idm.bdp.ninja.utils.miniparser;

public interface ConsumerExtended extends Consumer {
	boolean before(Token t);
	boolean after(Token t);
}
