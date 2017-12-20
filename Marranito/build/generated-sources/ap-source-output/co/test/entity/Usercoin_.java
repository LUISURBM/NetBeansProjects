package co.test.entity;

import co.test.entity.Coin;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-11T10:07:11")
@StaticMetamodel(Usercoin.class)
public class Usercoin_ { 

    public static volatile SingularAttribute<Usercoin, String> password;
    public static volatile ListAttribute<Usercoin, Coin> coinList;
    public static volatile SingularAttribute<Usercoin, Integer> id;
    public static volatile SingularAttribute<Usercoin, String> username;

}