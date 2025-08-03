package customer.service;

import java.util.List;

public interface PermissionService {
    List<String> getUserPermissions(Integer userId);
}
