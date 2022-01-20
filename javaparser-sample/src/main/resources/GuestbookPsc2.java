package exm.sale.var.psc.impl;

import java.sql.SQLRecoverableException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import hlicp.aco.sale.var.csc.CodeMangCsi;
import hlicp.aco.sale.var.csc.dto.CodeDto;
import hlicp.aco.sale.var.csc.impl.CodeMangCsc;
import hlicp.exm.sale.var.dsc.GuestbookDsi;
import hlicp.exm.sale.var.dsc.jpa.Member;
import hlicp.exm.sale.var.dsc.jpa.MemberRepositoryDsi;
import hlicp.exm.sale.var.interceptor.SampleServiceMessageRequestHandlerInterceptor;
import hlicp.exm.sale.var.psc.GuestbookPsi;
import hlicp.exm.sale.var.psc.dto.GuestbookDto;
import hlicp.exm.sale.var.psc.dto.GuestbookListDto;
import hlicp.exm.sale.var.psc.dto.GuestbookWrapDto;
import hone.bom.annotation.ServiceId;
import hone.bom.annotation.ServiceName;
import hone.bom.async.AsyncManagerHolder;
import hone.bom.commons.message.BomMessage;
import hone.bom.commons.message.Message;
import hone.bom.commons.message.util.AdditionalMessageUtil;
import hone.bom.transaction.BomTransactionManagerHolder;
import hone.bom.util.StringUtils;
import hone.bom.web.context.OnlineContextUtil;
import hone.bom.web.integration.outbound.RestApiClient;
import hone.bom.web.integration.outbound.param.RestTransactionMode;
import hone.bom.web.integration.outbound.param.TargetServiceInfo;
import hone.ext.online.core.HlicpException;
import hone.ext.online.core.deferred.DeferredServiceTemplate;
import hone.ext.online.transaction.HlicpTransactionManagerHolder;
import hone.ext.online.web.integration.outbound.EsbClient;
import hone.ext.online.web.integration.outbound.FepClient;
import hone.ext.online.web.message.HlicpMessageHeader;

/**
 * 방명록 DB를 처리하기 위한 서비스 처리
 *
 * <pre>
 *  방명록 정보의 요청 서비스를 처리한다.
 * </pre>
 *
 * @author kichaelee
 * @version 1.0
 * @since 2019.06.14 init
 */
@Service
public class GuestbookPsc implements GuestbookPsi {

	private static final Logger log = LoggerFactory.getLogger( GuestbookPsc.class );

	/**
	 * 페이징 처리된 Guestbook 전체를 조회한다.
	 *
	 * <pre>
	 * <pre>
	 * @return guestbook list
	 */
	@Override
	@ServiceId( value="exmsalevar012r")
	@ServiceName("메세지목록 조회 nextKey paging처리")
	public List<GuestbookDto> getGuestbookNextKeyPaging(GuestbookDto guestbook){
		if(log.isDebugEnabled()) {
			log.debug("페이징처리 샘플 파라미터 점검 ={}", guestbook);
		}

		/*
		 * 페이지 사이즈를 전달 받아야 한다.
		 */
		/*if(guestbook.getPaging() == null){
			throw new HlicpException("FWKE00001");
		}*/
//		ApplicationContext ctx = OnlineContextUtil.getRuntimeContext().getApplicationContext();
//		String[] beans = ctx.getBeanDefinitionNames();
//		for(String b : beans) {
//			log.info(" ====>==================={}", b);
//		}
		//비동기 컴포넌트 호출 방법 1
		CodeDto code = new CodeDto();
		code.setCd("ds");
		AsyncManagerHolder.execute(CodeMangCsc.class, "getCode", new Object[] {code});

		//비동기 호출 방법 2
		Future<CodeDto> future = AsyncManagerHolder.executeCallable(new Callable<CodeDto>() {
			public CodeDto call() throws Exception {
				return codeMangCsi.getCode(code);
			}
		 });

		try {
			log.error("Callable Result = {}", future.get(10, TimeUnit.SECONDS));
			log.error("Callable Result = {}", future.isDone());
		} catch ( Exception e) {
			log.error("Callable Exception = {}", e);
		}


		//비즈니스 처리
		List<GuestbookDto> list = guestbookDsi.selectGuestbookNextKeyPaging(guestbook);
		return list;
	}

}
