package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {  // É IMPORTANTE IMPLEMENTAR O DAO DE INTERFACE, PORQUE SE A TECNOLOGIA MUDA DE MYSQL PRA SQL OU OUTRO BANCO, NÃO TEM ALTERAÇÕES
    void insert(Department obj);
    void update(Department obj);
    void deleteById(Integer id);
    Department findById(Integer id);
    List<Department> findAll();
}