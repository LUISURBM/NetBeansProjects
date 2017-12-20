package co.test.entity;

import co.test.entity.Usercoin;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-11T10:07:11")
@StaticMetamodel(Coin.class)
public class Coin_ { 

    public static volatile SingularAttribute<Coin, Usercoin> idUsuario;
    public static volatile SingularAttribute<Coin, BigInteger> valor;
    public static volatile SingularAttribute<Coin, BigInteger> cantidad;
    public static volatile SingularAttribute<Coin, Integer> id;

}