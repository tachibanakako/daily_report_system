package filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {
       
    /**
     * @see HttpFilter#HttpFilter()
     */
    public LoginFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        // place your code here
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String servletPath = ((HttpServletRequest) request).getServletPath();
        
        if (servletPath.matches("/css.*")) {
            chain.doFilter(request, response);
        }else {
            HttpSession session = ((HttpServletRequest) request).getSession();
            
            String action = request.getParameter(ForwardConst.ACT.getValue());
            String command = request.getParameter(ForwardConst.CMD.getValue());
            
            EmployeeView ev = (EmployeeView) session.getAttribute(AttributeConst.LOGIN_EMP.getValue());
            //未ログイン
            if(ev == null) {
                if(!(ForwardConst.ACT_AUTH.getValue().equals(action)
                        && (ForwardConst.CMD_SHOW_LOGIN.getValue().equals(command)
                                || ForwardConst.CMD_LOGIN.getValue().equals(command)))) {
                    
                    ((HttpServletResponse) response).sendRedirect(
                            contextPath
                                   + "?action=" + ForwardConst.ACT_AUTH.getValue()
                                   + "&command=" + ForwardConst.CMD_SHOW_LOGIN.getValue());
                    return;
                }
            }else {
                //ログイン済み
                if(ForwardConst.ACT_AUTH.getValue().equals(action)) {
                    if(ForwardConst.CMD_SHOW_LOGIN.getValue().equals(command)) {
                        ((HttpServletResponse) response).sendRedirect(
                                contextPath
                                       + "?action=" + ForwardConst.ACT_TOP.getValue()
                                       + "&command=" + ForwardConst.CMD_INDEX.getValue());
                        return;
                        
                    }else if(ForwardConst.CMD_LOGOUT.getValue().equals(command)) {
                        //ログアウトの実施は許可
                    }else {
                        //上記以外の認証系Actionはエラー画面
                        String forward = String.format("/WEB-INF/views/%s.jsp", "error/unknown");
                        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);
                        dispatcher.forward(request, response);
                        
                        return;
                    }
                }
            }
            chain.doFilter(request, response);
        }
        
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}
