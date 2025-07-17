package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.chiendt.dto.response.PermissionResponse;
import vn.chiendt.model.Role;

import java.util.List;

@Repository
public interface RoleAndPermissionRepository extends JpaRepository<Role,Integer> {
    @Query(value = "select distinct r.id from Role r " +
            "inner join UserHasRole ur on ur.role.id = r.id " +
            "inner join User u on u.id = ur.user.id " +
            "where u.username = :username")
    List<Long> findRolesByUsername(String username);

    @Query(value = "select new vn.chiendt.dto.response.PermissionResponse(p.id, p.name, p.description) from Permission p " +
            "where p.id in (select distinct rp.permission.id from RoleHasPermission rp " +
            "inner join Role r on r.id = rp.role.id " +
            "where r.id = :roleId)")
    List<PermissionResponse> findPermissionsByRoleId(Integer roleId);

    @Query(value = "select new vn.chiendt.dto.response.PermissionResponse(p.id, p.name, p.description) " +
            "from Permission p where p.id in (select distinct rp.permission.id " +
            "from RoleHasPermission rp inner join Role r1 on r1.id = rp.role.id " +
            "where r1.id in (select distinct r2.id from Role r2 inner join UserHasRole ur on ur.role.id = r2.id " +
            "inner join User u on u.id = ur.user.id " +
            "where u.username = :username))")
    List<PermissionResponse> findPermissionsByUsername(String username);

    @Query(value = "select count(p.id) from Permission p " +
            "where p.description = :requestPath and p.id in (select distinct rp.permission.id " +
            "from RoleHasPermission rp inner join Role r1 on r1.id = rp.role.id " +
            "where r1.id in (select distinct r2.id from Role r2 inner join UserHasRole ur on ur.role.id = r2.id " +
            "inner join User u on u.id = ur.user.id " +
            "where u.username = :username))")
    Long countPermissionByRequestPathAndUser(String requestPath, String username);
}
