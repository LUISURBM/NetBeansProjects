package co.ms.entity;

import co.ms.entity.Denomination;
import co.ms.entity.Userpurse;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-20T10:16:39")
@StaticMetamodel(Profit.class)
public class Profit_ { 

    public static volatile SingularAttribute<Profit, Userpurse> idUsuario;
    public static volatile SingularAttribute<Profit, BigInteger> cantidad;
    public static volatile SingularAttribute<Profit, Integer> id;
    public static volatile SingularAttribute<Profit, Denomination> idDenomination;

}