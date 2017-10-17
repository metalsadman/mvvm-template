package com.duyp.architecture.mvvm.data.repository;

import android.support.annotation.NonNull;

import com.duyp.androidutils.realm.LiveRealmResults;
import com.duyp.architecture.mvvm.data.remote.GithubService;
import com.duyp.architecture.mvvm.data.source.Resource;
import com.duyp.architecture.mvvm.local.RealmDatabase;
import com.duyp.architecture.mvvm.local.dao.IssueDao;
import com.duyp.architecture.mvvm.local.dao.RepositoryDao;
import com.duyp.architecture.mvvm.model.Issue;
import com.duyp.architecture.mvvm.model.Repository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import lombok.Getter;
import retrofit2.http.GET;

/**
 * Created by duypham on 9/17/17.
 *
 */

@Getter
public class IssuesRepo extends BaseRepo {

    private Repository mRepository;

    private IssueDao mIssuesDao;

    @Getter
    private LiveRealmResults<Issue> data;

    @Inject
    public IssuesRepo(GithubService githubService, RealmDatabase realmDatabase) {
        super(githubService, realmDatabase);
        this.mIssuesDao = realmDatabase.newIssueDao();
    }

    public void initRepo(@NonNull Long repoId) {
        RepositoryDao repositoryDao = getRealmDatabase().newRepositoryDao();
        this.mRepository = repositoryDao.getById(repoId).getData();
        data = mIssuesDao.getRepoIssues(mRepository.getId());
        repositoryDao.closeRealm();
    }

    public Flowable<Resource<List<Issue>>> getRepoIssues() {
        return createRemoteSourceMapper(getGithubService().getRepoIssues(mRepository.getOwner().getLogin(), mRepository.getName()), issues -> {
            for (Issue issue : issues) {
                issue.setRepoId(mRepository.getId());
            }
            mIssuesDao.addAll(issues);
        });
    }

    @Override
    public void onDestroy() {
        mIssuesDao.closeRealm();
    }
}