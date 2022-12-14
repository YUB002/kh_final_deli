package kh.deli.domain.main.controller;

import kh.deli.domain.main.service.AccountService;
import kh.deli.global.entity.AccountDTO;
import kh.deli.global.util.redis.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/account/")
public class AccountController {

    private final AccountService accountService;
    private final HttpSession session;
    private final RedisUtil redisUtil;


    /**
     * <h1>Normal Type 회원 로그인</h1>
     * @param email
     * @param pw
     * @param emailSave
     * @param response
     * @return set loginEmail to Session & set saved_email to Cookie
     * @throws Exception
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String email, String pw, String emailSave, HttpServletResponse response) throws Exception {
        // 로그인 서비스 요청
        int result = accountService.login(email, pw);

        if (result == 1) { // 로그인 성공했을 때

            // Session 에 로그인 성공한 이메일 담기
            session.setAttribute("loginEmail", email);
            session.setAttribute("loginType", "normal");

            // String.valueOf 사용하지 않으면 NullPointException
            // String.valueOf 없이 사용하려면 true false 등으로 값 변환 후 조건문 작성
            if (String.valueOf(emailSave).equals("on")) { // [이메일 기억하기] 체크 되었을 때
                // "saved_email"이라는 쿠키명에 email 담기
                Cookie cookie = new Cookie("saved_email", email);
                cookie.setMaxAge(60 * 60 * 24 * 14); // 유통기한 14일 (초 단위)
                cookie.setPath("/"); // 모든 경로에서 사용하겠다 선언
                response.addCookie(cookie); // response 에 쿠키 생성
            } else{ // [이메일 기억하기] 체크 안 되었을 때
                Cookie cookie = new Cookie("saved_email", null);
                cookie.setMaxAge(0); // 유통기한 0초 ( 삭제 )
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        return "redirect:/";
    }

    @RequestMapping("logout")
    public String logout() throws Exception {
        String loginType = (String)session.getAttribute("loginType");
        switch (loginType) {
            case "normal" :
                session.invalidate();
                break;
            case "kakao" :
                return "redirect:https://kauth.kakao.com/oauth/logout?client_id=1475b617eab69841d5cabd68f1527015&logout_redirect_uri=http://localhost/account/oauth/kakaoLogout";
                }
        return "redirect:/";
    }

    @RequestMapping("toMemberSignUp")
    public String toMemberSignUp() throws Exception {
        return "main/memberSignUp";
    }

    @PostMapping("memberSignUp")
    public String memberSignUp(AccountDTO accountDTO) throws Exception {
        accountService.memberSignUp(accountDTO);
        session.setAttribute("loginEmail", accountDTO.getAcc_email());
        session.setAttribute("loginType", "normal");
        return "redirect:/";
    }

    @RequestMapping("toKakaoSignUp")
    public String toKakaoSignUp(String kakaoId, Model model) throws Exception {
        model.addAttribute("acc_token", kakaoId);
        return "main/kakaoSignUp";
    }

    @PostMapping("kakaoSignUp")
    public String kakaoSignUp(AccountDTO accountDTO) throws Exception {
        accountService.kakaoSignUp(accountDTO);
        session.setAttribute("loginEmail", accountDTO.getAcc_email());
        session.setAttribute("loginType", "kakao");
        System.out.println("야 카카오 회원가입 성공했다 짜식들아");
        return "redirect:/";
    }

    @RequestMapping("oauth/kakao")
    public String  kakaoLogin(String code) throws Exception {
        // 코드를 이용하여 accessToken 추출
        String accessToken = accountService.getKakaoAccessToken(code);
        // accessToken을 이용하여 사용자 정보 추출
        String kakaoId = accountService.getKakaoId(accessToken);
        System.out.println("로그인 성공! 저장은 아직!");
        // kakaoId 으로 카카오 회원 정보 DB 저장
        if(!accountService.dupleCheckKakaoId(kakaoId)){
            System.out.println("로그인 성공! 저장은 할 예정!");
            // 회원가입으로 페이지 이동
            return "redirect:/account/toKakaoSignUp?kakaoId=" + kakaoId;
        } else {
            // 저장된 회원 정보가 있으면 회원가입 된게 맞아서 그냥 페이지 메인으로
            session.setAttribute("loginEmail", accountService.getAccEmail(kakaoId));
            session.setAttribute("kakaoAccessToken", accessToken);
            session.setAttribute("loginType", "kakao");
            return "redirect:/";
        }
    }

    @RequestMapping("oauth/kakaoLogout")
    public String  kakaoLogout() throws Exception {
        session.invalidate();
        return "redirect:/";
    }

    @ResponseBody
    @RequestMapping(value="certify/tel", method=RequestMethod.POST)
    public String telCertify(String tel) {
        String serverTelCertifyStr = accountService.sendRandomMessage(tel);
        redisUtil.setData(tel,serverTelCertifyStr);
        System.out.println(tel);
        System.out.println(serverTelCertifyStr);
        return serverTelCertifyStr;
    }

    @ResponseBody
    @RequestMapping(value="certify/telConfirm", method=RequestMethod.POST)
    public boolean telConfirm(String tel, String telCertifyStr) {
        System.out.println("내가 적은 연락처: " + tel);
        System.out.println("내가 적은 인증문자: " + telCertifyStr);
        String getServerTelCertifyStr = redisUtil.getData(tel);
        System.out.println("연락처로 얻은-입력해둔 랜덤문자: " + getServerTelCertifyStr);
        return telCertifyStr.equals(getServerTelCertifyStr) ? true : false;
    }

}

