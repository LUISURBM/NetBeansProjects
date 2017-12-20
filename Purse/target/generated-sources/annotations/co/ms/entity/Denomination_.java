package co.ms.entity;

import co.ms.entity.Profit;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-20T10:16:39")
@StaticMetamodel(Denomination.class)
public class Denomination_ { 

    public static volatile SingularAttribute<Denomination, String> dnmType;
    public static volatile SingularAttribute<Denomination, BigInteger> valor;
    public static volatile SingularAttribute<Denomination, Integer> id;
    public static volatile ListAttribute<Denomination, Profit> profitList;

}