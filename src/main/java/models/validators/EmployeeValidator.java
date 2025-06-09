package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.EmployeeView;
import constants.MessageConst;
import services.EmployeeService;


public class EmployeeValidator {
//従業員インスタンスの各項目のバリデーション
    public static List<String> validate(
            EmployeeService service, EmployeeView ev, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag){
        List<String> errors =new ArrayList<String>();
        
        //社員番号チェック
        String codeError = validateCode(service, ev.getCode(), codeDuplicateCheckFlag);
        if (!codeError.equals("")) {
            errors.add(codeError);
        }
        
        //氏名チェック
        String nameError = validateName(ev.getName());
        if (!nameError.equals("")) {
            errors.add(nameError);
        }
        
        //パスワードチェック
        String passError = validatePassword(ev.getPassword(), passwordCheckFlag);
        if (!passError.equals("")) {
            errors.add(passError);
        }
        
        return errors;
    }
    //入力チェック、エラーメッセージ返却
    private static String validateCode(EmployeeService service, String code, Boolean codeDuplicateCheckFlag) {
        //入力値なし
        if(code == null || codeDuplicateCheckFlag.equals("")) {
            return MessageConst.E_NOEMP_CODE.getMessage();
        }
        //社員番号重複チェック
        if(codeDuplicateCheckFlag) {
            long employeesCount = isDuplicateEmployee(service, code);
            
            if (employeesCount > 0) {
                return MessageConst.E_EMP_CODE_EXIST.getMessage();
            }
        }
        return "";
    }
    //社員番号のデータ件数
    private static long isDuplicateEmployee(EmployeeService service, String code) {
        long employeesCount = service.countByCode(code);
        return employeesCount;
    }
    //氏名に入力値があるかチェック、なければエラーメッセージ返却
    public static String validateName(String name) {
    if (name == null || name.equals("")) {
        return MessageConst.E_NONAME.getMessage();
        }
    return "";
    }
    //パスワードの入力チェック、エラーメッセージ返却
    private static String validatePassword(String password, Boolean passwordCheckFlag) {
        if (passwordCheckFlag && (password == null || password.equals(""))) {
            return MessageConst.E_NOPASSWORD.getMessage();
        }
        return "";
    }

}
