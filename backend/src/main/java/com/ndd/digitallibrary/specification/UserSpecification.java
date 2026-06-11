package com.ndd.digitallibrary.specification;

import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.enums.Role;
import com.ndd.digitallibrary.enums.SubscriptionTier;
import com.ndd.digitallibrary.enums.UserStatus;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    private UserSpecification(){}

    public static Specification<User> hasKeyword(String keyword){
        return (root, query, cb) -> {
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("fullName")), pattern)
            );
        };
    }

    public static Specification<User> hasRole(Role role){
        return (root, query, cb) -> {
            if(role == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("role"), role);
        };
    }

    public static Specification<User> hasSubscription(SubscriptionTier subscriptionTier){
        return (root, query, cb) -> {
            if(subscriptionTier == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("subscriptionTier"), subscriptionTier);
        };
    }

    public static Specification<User> hasUserStatus(UserStatus userStatus){
        return (root, query, cb) -> {
            if(userStatus == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("userStatus"), userStatus);
        };
    }
}
