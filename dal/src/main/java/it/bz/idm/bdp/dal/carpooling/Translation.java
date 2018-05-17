package it.bz.idm.bdp.dal.carpooling;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ColumnDefault;

@Entity
public abstract class Translation {

	@Id
	@GeneratedValue(generator = "translation_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "translation_gen", sequenceName = "translation_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('translation_seq')")
	private Long id;

	public Translation() {
	}

}
