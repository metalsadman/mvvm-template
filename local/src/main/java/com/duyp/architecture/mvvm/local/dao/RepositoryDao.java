package com.duyp.architecture.mvvm.local.dao;

import com.duyp.androidutils.realm.BaseRealmDao;
import com.duyp.androidutils.realm.LiveRealmResults;
import com.duyp.architecture.mvvm.model.Repository;

/**
 * Created by duypham on 9/18/17.
 * Realm Repository Data Access Object
 */

public interface RepositoryDao extends BaseRealmDao<Repository> {

//    /**
//     * Get first n saved repositories
//     * @param limit maximum number of repositories
//     * @return RealmResults
//     */
//    RealmResults<Repository> getAllRepositories(int limit);

    /**
     * Cearch repositories which have name like input name
     * @param repoName repository name, not user name
     * @return RealmResults
     */
    LiveRealmResults<Repository> getRepositoriesWithNameLike(String repoName);

    /**
     * Get all repositories of given user
     * @param userLogin {@link com.duyp.architecture.mvvm.model.User#login}
     * @return RealmResults
     */
    LiveRealmResults<Repository> getUserRepositories(String userLogin);
}