package model.dao;

import db.DB;
import model.dao.impl.SellerDaoJDBC;
import model.dao.impl.DepartmentDaoJDBC;

public class DaoFactory {

    public static SellerDao createSellerDao() { // RETORNA O TIPO DA INTERFACE INSTANCIANDO UM OBJETO DE IMPLEMENTAÇÃO, CAMUFLA A IMPLEMENTAÇÃO
        return new SellerDaoJDBC(DB.getConnection());
    }
    
    public static DepartmentDao createDepartmentDao() { // RETORNA O TIPO DA INTERFACE INSTANCIANDO UM OBJETO DE IMPLEMENTAÇÃO, CAMUFLA A IMPLEMENTAÇÃO
        return new DepartmentDaoJDBC(DB.getConnection());
    }
    
}