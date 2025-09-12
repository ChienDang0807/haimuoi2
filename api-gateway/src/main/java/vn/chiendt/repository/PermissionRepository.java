package vn.chiendt.repository;

import org.springframework.data.repository.CrudRepository;
import vn.chiendt.model.PermissionHash;

public interface PermissionRepository extends CrudRepository<PermissionHash, String> {
}
