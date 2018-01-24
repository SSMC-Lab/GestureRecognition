package com.example.monster.airgesture.ui.user;

import com.example.monster.airgesture.data.DataFactory;
import com.example.monster.airgesture.data.IUserDAO;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.ui.base.BasePresenter;
import com.example.monster.airgesture.utils.AlphabetUtils;
import com.example.monster.airgesture.utils.LogUtils;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.List;

/**
 * Presenter实现类 {@link IUserCreatorContract.Presenter}
 * Created by Welkinshadow on 2018/1/21.
 */

public class UserCreatorPresenter<V extends IUserCreatorContract.View> extends BasePresenter<V>
        implements IUserCreatorContract.Presenter<V> {

    private IUserDAO userDAO;
    private List<Integer> letterMapping;

    public UserCreatorPresenter() {
        userDAO = DataFactory.getUserDAO();
        letterMapping = userDAO.getDefaultUser().getLetterMapping();//初始值为默认构造序列
    }

    @Override
    public void saveLetterMapping(List<String> selectedLetter, int code) {
        for (String letter : selectedLetter) {
            if (!StringUtils.isEmpty(letter) && letter.length() <= 1) {
                int position = AlphabetUtils.INSTANCE.getPosition(letter.charAt(0));
                letterMapping.set(position, code);
            } else {
                LogUtils.e("letter length > 1");
                getView().createFailed("字符长度大于1");
            }
        }
    }

    @Override
    public void createUserByHabit(String userName) {
        User user = new User(userName, letterMapping);
        userDAO.createDictionaryByHabit(user, new IUserDAO.OnCreateListener() {
            @Override
            public void OnStart() {
                getView().showLoading();
            }

            @Override
            public void OnSuccessful() {
                getView().hideLoading();
                getView().createSuccessful();
            }

            @Override
            public void OnFailed() {
                getView().hideLoading();
                getView().createFailed("未知原因导致的错误");
            }
        });
    }
}
