package jnshu.taskseven.service;import jnshu.taskseven.model.Student;import java.util.List;/** * student在service接口 * @author Administrator * @date 7/8/2017 */public interface StudentService {     /**      * get all student information      * 获取全部用户信息      * @param      * @return 返回学生列表的全部信息 List<Student>      */     List<Student> listStudentTable ();     /**      * get student information      * 获取用户信息      * @param  user      * @return 返回学生的全部信息 Student      */     Student getStudentUser(String user);     /**      * by rule Insert user information the student      * 插入用户信息      * @param  student      * @return 返回插入的记录数量，可以用getId()获取到自增的Id int      */     Integer insertStudentUser(Student student);     /**      * fetch data by rule user number      * 用户名、学习状态、id、职业四选一      * @param  student(user or status)      * @return 条件查询下用户的数量 int      */     Integer countStudentUser(Student student);     /**      * by user name modify user picture、phone、email      * @param student      * @return      */     Integer updateStudentUserPicturePhoneEmail(Student student);}