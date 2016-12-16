package it.bz.idm.bdp.dal.carpooling;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public abstract class Translation {
	
	@Id
	@GeneratedValue(generator="translationvalue",strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(name="translationvalue", sequenceName = "translation_seq",schema="intime",allocationSize=1)
	private Long id;
	
	public Translation() {
	}
	
}
