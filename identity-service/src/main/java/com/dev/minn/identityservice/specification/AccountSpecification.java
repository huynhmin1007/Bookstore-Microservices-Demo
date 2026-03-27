package com.dev.minn.identityservice.specification;

import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.entity.Account;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountSpecification {

    public static Specification<Account> buildSearchQuery(String keyword, AccountStatus status, String role) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("email")), likePattern)
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(cb.notEqual(root.get("status"), AccountStatus.DELETED));
            }

            if (StringUtils.hasText(role)) {
                predicates.add(cb.equal(
                        root.join("roles").join("role").get("name"), role
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
