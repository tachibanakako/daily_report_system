package actions;

import java.io.IOException;

import jakarta.servlet.ServletException;

import constants.ForwardConst;

public class UnknownAction extends ActionBase {
    //共通エラー画面の表示
    @Override
    public void process() throws ServletException, IOException{
        forward(ForwardConst.FW_ERR_UNKNOWN);
    }

}
