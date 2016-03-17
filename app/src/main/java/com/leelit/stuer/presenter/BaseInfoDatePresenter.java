package com.leelit.stuer.presenter;

import com.leelit.stuer.bean.DatingInfo;
import com.leelit.stuer.constant.NetConstant;
import com.leelit.stuer.model.DateModel;
import com.leelit.stuer.viewinterface.IBaseInfoView;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Leelit on 2016/3/8.
 */
public class BaseInfoDatePresenter implements IPresenter{

    private DateModel mModel = new DateModel();

    private IBaseInfoView mView;
    private Subscriber<List<DatingInfo>> mSubscriber1;
    private Subscriber<ResponseBody> mSubscriber2;

    public BaseInfoDatePresenter(IBaseInfoView view) {
        mView = view;
    }

    public void doLoadingInfos(String type) {
        mSubscriber1 = new Subscriber<List<DatingInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.notRefreshing();
                mView.netError();
            }

            @Override
            public void onNext(List<DatingInfo> datingInfos) {
                mView.notRefreshing();
                mView.showInfos(datingInfos);
                if (datingInfos.isEmpty()) {
                    mView.noInfos();
                }
            }
        };
        mModel.getGroupRecords(type, mSubscriber1);
    }


    public void doPostInfo(final DatingInfo info) {
        mView.showPostProgressDialog();
        mSubscriber2 = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e.toString().split(" ")[2].equals(String.valueOf(NetConstant.NET_ERROR_RECORD_EXISTED))) {
                    mView.infoExist();
                } else {
                    mView.netError();
                }
                mView.dismissPostProgressDialog();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                mView.dismissPostProgressDialog();
                mView.afterPostInfo();
            }
        };
        mModel.addRecord(info, mSubscriber2);
    }

    @Override
    public void doClear() {
        if (mSubscriber1 != null) {
            mSubscriber1.unsubscribe();
        }
        if (mSubscriber2 != null) {
            mSubscriber2.unsubscribe();
        }
        mView = null;
    }
}
