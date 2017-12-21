package co.ms.entity;

import co.ms.entity.Profit;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-21T15:22:19")
@StaticMetamodel(Userpurse.class)
public class Userpurse_ { 

    public static volatile SingularAttribute<Userpurse, String> password;
    public static volatile SingularAttribute<Userpurse, Integer> id;
    public static volatile ListAttribute<Userpurse, Profit> profitList;
    public static volatile SingularAttribute<Userpurse, String> username;

}