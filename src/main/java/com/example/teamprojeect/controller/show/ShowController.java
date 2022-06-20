package com.example.teamprojeect.controller.show;


import com.example.teamprojeect.domain.vo.list.ListDTO;
import com.example.teamprojeect.domain.vo.paging.Criteria;
import com.example.teamprojeect.domain.vo.paging.show.ShowPageDTO;
import com.example.teamprojeect.domain.vo.show.ShowVO;
import com.example.teamprojeect.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/concert/*")
@Slf4j
@RequiredArgsConstructor
public class ShowController {
    // 필드 생성
    @Autowired
    private ShowService showService;

    // 진행 예정 공연 리스트 페이지 이동
    @GetMapping("/concertPlanList")
    public String goConcertPlan(Criteria criteria, ListDTO listDTO, Model model, Long showNumber) {
        List<ShowVO> showList = showService.getList(criteria, listDTO);

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

        showList.forEach(showVO -> {
            // 지역만 선택
            String showAddress = showVO.getShowAddress();
            showAddress = showAddress.substring(0, 2);

            String showLocation = showVO.getShowLocation();
            showLocation = "[" + showAddress + "] " + showLocation;
            showVO.setShowLocation(showLocation);

            // dday 계산
            String showDay = showVO.getShowDay();
            String todayDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())); // 오늘날짜

            try {
                Date date = new Date(dayFormat.parse(showDay).getTime());
                Date today = new Date(dayFormat.parse(todayDay).getTime());
                long calculate = date.getTime() - today.getTime();
                int Ddays = (int) (calculate / ( 24*60*60*1000));
                showVO.setDDay(Ddays);
            } catch (ParseException e) {
                System.err.println("dateStr : " + showDay + ", datePattern:" + dayFormat);
                e.printStackTrace();
            }
        });


        model.addAttribute("showList", showList);
        model.addAttribute("showPageDTO", new ShowPageDTO(criteria, showService.getTotal(listDTO)));
        return "concertPlan/concertPlanList";
    }

    @GetMapping("/concertPlanArtistType")
    @ResponseBody
    public String goConcertPlanA(){

        return "showService.getList()";
    }

    @GetMapping("/concertPlanMusician")
    @ResponseBody
    public String goConcertPlanM(){
        return "concertPlan/concertPlanMusician";
    }

    @GetMapping("/concertPlanPerformance")
    @ResponseBody
    public String goConcertPlanP(){
        return "concertPlan/concertPlanPerformance";
    }

    // 진행 예정 공연 상세보기 페이지 이동
    @GetMapping("/concertPlanInfo")
    public String goConcertInfo(Long showNumber, Criteria criteria, HttpServletRequest request, Model model) {
        String requestURL = request.getRequestURI();
        log.info(requestURL.substring(requestURL.lastIndexOf("/")));
        ShowVO showVO = showService.read(showNumber);
        if(showVO.getShowType() == 1) {
            showVO.setShowCategory("뮤지션");
        } else if (showVO.getShowType() == 2) {
            showVO.setShowCategory("퍼포먼스");
        }

        try {

            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String showDay = showVO.getShowDay();
            Date day = dayFormat.parse(showDay);
            showDay = dayFormat.format(day);
            showVO.setShowDay(showDay);

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            String showTime = showVO.getShowTime();
            Date time = timeFormat.parse(showTime);
            showTime = timeFormat.format(time);
            showVO.setShowTime(showTime);
        } catch (ParseException e) {
            System.err.println("dateStr : "  + ", datePattern:");
            e.printStackTrace();
        }


        model.addAttribute("concert", showVO);
        return "concertPlan/concertPlanInfo";
    }

    // 진행 예정 공연 수정 페이지 이동
    @GetMapping("/concertPlanModify")
    public String goConcertPlanModify() {
        return "concertPlan/concertPlanModify";
    }

    // 진행 예정 공연 수정 완료

    // 진행 예정 공연 등록 페이지 이동
    @PostMapping("/concertPlanRegister")
    public RedirectView goConcertPlanRegister(ShowVO showVO, RedirectAttributes rttr) {
        showVO.setArtistNumber(1L);
        showService.register(showVO);
        rttr.addFlashAttribute("showNumber", showVO.getShowNumber());
        return new RedirectView("/concert/concertPlanList");
    }

    @GetMapping("/concertPlanRegister")
    public String goConcertPlanRegister() {
        return "concertPlan/concertPlanRegister";
    }

    // 진행 예정 공연 등록 완료

    // 진행중인 콘서트 페이지
    @GetMapping("/concertLive")
    public String goConcertLive() {
        return "concert/concertLive";
    }

    @PostMapping("/concertDelete")
    public String remove(Long showNumber, Criteria criteria, ListDTO listDTO, Model model) {
        showService.remove(showNumber);
        return goConcertPlan(criteria, listDTO, model, showNumber);
    }
}