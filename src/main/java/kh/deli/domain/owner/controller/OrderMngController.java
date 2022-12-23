package kh.deli.domain.owner.controller;

import kh.deli.domain.owner.dto.OwnerOrderMngResponseDTO;
import kh.deli.domain.owner.dto.StoreNameAndSeqRequestDTO;
import kh.deli.domain.owner.service.OwnerOrderMngService;
import kh.deli.domain.owner.service.OwnerOrdersService;
import kh.deli.domain.owner.service.OwnerOwnerService;
import kh.deli.domain.owner.service.OwnerStoreService;
import kh.deli.global.exception.PermissionException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

//optional로 떡칠해보기
/**
 * <h2>개발단계 DB수정시 기본값 점검요함</h2>
 */
@Controller
@AllArgsConstructor
@RequestMapping("/owner/order")
public class OrderMngController {
    private final OwnerOrdersService ordersService;
    private final OwnerStoreService storeService;
    private final OwnerOrderMngService mngService;
    private final OwnerOwnerService ownerService;

    private final HttpSession session;

    @RequestMapping("")
    public String toPage() {
        Optional<Integer> ownerAccSeqOptional = Optional.ofNullable((Integer) session.getAttribute("acc_seq"));
        int ownerAccSeq = ownerAccSeqOptional.orElse(31);
        int ownerSeq = ownerService.convertAccSeqToOwnerSeq(ownerAccSeq); //(sample data)

        List<StoreNameAndSeqRequestDTO> storeList = storeService.getStoreListByOwnerSeq(ownerSeq);

        Optional<Integer> firstStoreSeq = Optional.ofNullable(storeList.get(0).getStore_seq());

        return "redirect:/owner/order/" + firstStoreSeq.orElse(21); //없으면 기본값 21(sample data)
    }

    @RequestMapping("/{storeSeq}")
    public String toMngPage(Model model, @PathVariable(value = "storeSeq") Integer inputStoreSeq) {

        Optional<Integer> ownerAccSeqOptional = Optional.ofNullable((Integer) session.getAttribute("acc_seq"));
        int ownerAccSeq = ownerAccSeqOptional.orElse(31); //없으면 기본값 31(sample data)
        int ownerSeq = ownerService.convertAccSeqToOwnerSeq(ownerAccSeq); //(sample data)
        int storeSeq = Optional.ofNullable(inputStoreSeq).orElse(21); // 기본값 21(sample data)

        //storeList 만들기
        //사업자별 가게주문관리 전환 출력을 위한 List
        List<StoreNameAndSeqRequestDTO> storeList = storeService.getStoreListByOwnerSeq(ownerSeq);

        //session 으로 유효성 검사.
        int ownerSeqBySession = ownerService.convertAccSeqToOwnerSeq(ownerAccSeq);
        int ownerSeqByInputStore = storeService.getOwnerByStore(storeSeq);

        if (ownerSeqByInputStore != ownerSeqBySession) {
            throw new PermissionException();
        }

        //orderList 만들기
        List<OwnerOrderMngResponseDTO> orderList =  mngService.getOrderMngResponseDTO(storeSeq);

        model.addAttribute("storeList", storeList);
        model.addAttribute("orderList", orderList);

        return "owner/orderMng";
    }
}