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


	public static boolean test = false;

	public static int timeout = 2000;

	@Autowired(required=false)
	private GuestbookDsi guestbookDsi;

	@Autowired(required=false)
	private CodeMangCsi codeMangCsi;

	@Autowired(required=false)
	private MemberRepositoryDsi memberRepositoryDsi;

    @Autowired(required = false)
    private DeferredServiceTemplate deferredServiceTemplate;

    @Autowired
    private RestApiClient client;

    @Autowired
    private FepClient fepClient;

    @Autowired
    private EsbClient esbClient;

	@Override
	@ServiceId("exmsalevar0011r")
	@ServiceName("메세지단 조회")
	public HashMap geGuestbook2(HashMap input){
		log.error("----------------333----------{}", input);
		TargetServiceInfo serviceInfo = new TargetServiceInfo("mci","/hlicp/exm/exmsalevar001r");
		serviceInfo.setRestTransactionMode(RestTransactionMode.Yes);
		HashMap aa = client.sendAndReceive(serviceInfo, input, HashMap.class);
		if(log.isInfoEnabled()) {
			log.info("aa={}",  aa);
		}
		return aa;
	}
	/**
	 * Guestbook 단건을 조회한다.
	 *
	 * <pre>
	 *
	 * <pre>
	 * @return guestbook list
	 */
	@Override
	@ServiceId(value = "exmsalevar001r")
	@ServiceName("메세지단 조회")
	//@Transactional(timeout = 3)
	public GuestbookDto geGuestbook(GuestbookDto input){

		GuestbookDto output = guestbookDsi.selectGuestbook(input);

		try {
			throw new HlicpException("COMS1002");
		}catch(HlicpException e) {
			log.warn(" {}", e.toString());
		}
		/*로그인 후 사용자 정보 취득하는 방법
		 *
		   UserDetails u = OnlineContextUtil.getAuthenticationUser(UserDetails.class);
  		   log.info("user details={}", u.getUsername());
		 */

		//메시지 추가 하는 방
		AdditionalMessageUtil.addMessageWithLinkedMessage("COMS1002", new String[] {"xptmxm"}, "B505", "이것은 테스트");
		AdditionalMessageUtil.addMessageWithLinkedMessage("COMS1002", null, "B506", "이것은 테스트2");



		//메시지 추출 방법
		List<Message>  list = AdditionalMessageUtil.getMessages();
		for(Message  m : list) {
			BomMessage bm = (BomMessage)m;
			log.error("===메시지 정보  ==>{},{},{}", bm.getMessage(), bm.getMessageId(), bm.getLinkedMessage());
			log.error("====메시지 연결 메시지 정보 =>{}", bm.getLinkedMessage().get("B505"));
		}

		log.error("======size===============>{}", list.size());

		CodeDto code = new CodeDto();
		code.setCd("ds");
		code = codeMangCsi.getCode(code);
			log.error("컴포넌트 호출 결과={}", code);

		if("true".contentEquals(input.getRollback()) ) {
			 throw new IllegalStateException ( "Max number of active transactions reached:250"  );

		}

		return output;
	}
	@Override
	@ServiceId(value = "exmsalevar110r")
	@ServiceName("Exception테스트 ")
	public GuestbookDto geGuestbook2(GuestbookDto input) throws SQLRecoverableException{

		if("false".contentEquals(input.getRollback()) ) {
			test = false;
		}else {
			test = true;
		}

		timeout = input.getTimeout();

		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return input;
//
//		GuestbookDto output = guestbookDsi.selectGuestbook(input);
//
//		if(true) {
//			throw new java.sql.SQLRecoverableException("test");
//		}
//		return output;
	}

	/**
	 * 페이징 처리된 Guestbook 전체를 조회한다.
	 *  - 인터셉터 적용 테스트용
	 *  - @Cacheable 적용 테스트용
	 *  - Exception 및 메시지 처리 테스트용
	 *  - 전문 헤더값 취득 방법
	 * <pre>
	 * <pre>
	 * @return guestbook list
	 */
	@Override
	@ServiceId( value="exmsalevar002r" ) //, applyGlobalInterceptors=true, interceptors=SampleServiceMessageRequestHandlerInterceptor.class)
	@ServiceName("메세지목록 조회_paging처리")
	public GuestbookListDto getGuestbookPagingList(GuestbookDto guestbook){
		if(log.isDebugEnabled()) {
			log.debug("페이징처리 샘플 파라미터 점검 ={}", guestbook);
		}

		HlicpMessageHeader header  = OnlineContextUtil.getHeader(HlicpMessageHeader.class);
//		if(log.isInfoEnabled()) {
//			log.info(">>헤더값 취득 방법 >>>>{}", header);
//			//log.info(">>>>>>{}", SystemConfigServiceHolder.getSystemConfigService().getAllConfig(true));
//		}
//
//	   	Authentication au = SecurityContextHolder.getContext().getAuthentication();
//	   	if(au != null) {
//	    	OAuth2AuthenticationDetails od = (OAuth2AuthenticationDetails)au.getDetails();
//	    	if(log.isInfoEnabled()) {
//		    	log.info("====od.getDecodedDetails()=======>{}",   od.getDecodedDetails());
//		    	log.info("=====od.getSessionId()======>{}",   od.getSessionId());
//		    	log.info("=====au.getPrincipal()======>{}",  au.getPrincipal());
//		    	log.info("OnlineContextUtil.getAttribute( )=====>{}" , OnlineContextUtil.getAttribute("_login_user_info__", Map.class));
//	    	}
//	   	}else {
//	   		HlicpUser user =  hone.ext.online.web.session.HttpSessionUtils.getUserInfo();
//	   		log.info("HttpSessionUtils.getUserInfo()=====>{}" , user);
//	   	}
//
//		/*
//		 * 비즈니스처리시 Exception을 발생시켜야 한다면 아래와 같이 한다
//		 */
//		if( StringUtils.isEmpty(guestbook.getPageNumber()) ){
//			throw new HlicpException("FWKE00001");
//		}
		//컴포넌트 호출
		CodeDto code = new CodeDto();
		code.setCd("ds");
		code = codeMangCsi.getCode(code);
		if(log.isDebugEnabled()) {
			log.debug("컴포넌트 호출 결과={}", code);
		}
		//비즈니스 처리
		List<GuestbookDto> list = guestbookDsi.selectGuestbookPagingList(guestbook);
		GuestbookListDto result = new GuestbookListDto();
		result.setGuestbooks(list);

		//페이지 화면처리를 위한 총건수 조회
		int totalCount = guestbookDsi.selectGuestbookCount(guestbook);
		header.setTotalCount(totalCount);

		//추가 메시지 테스트
		AdditionalMessageUtil.addMessage("COMS1002", "222");
		AdditionalMessageUtil.addTextMessage("testtest22");

		return result;
	}

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
	/**
	 * 페이징 처리된 Guestbook 전체를 조회한다.
	 *
	 * <pre>
	 * <pre>
	 * @return guestbook list
	 */
	@Override
	@ServiceId( value="exmsalevar013r")
	@ServiceName("파라미터 없는 메소드 테스트")
	public GuestbookDto getNoParameterTest(){
		GuestbookDto dto = new GuestbookDto();
		return dto;
	}

	/**
	 * 다건 입력 - List가 아규먼트인 경우 처리
	 *
	 * <pre>
	 * <pre>
	 * @return guestbook list
	 */
	@Override
	@ServiceId( value="exmsalevar011r")
	@ServiceName("다건입력-List일때 GenericType")
	public GuestbookDto getGuestbookListGenericTypeArgument(List<GuestbookDto> input){
		if(log.isDebugEnabled()) {
			log.debug("다건입 샘플 파라미터 점검 ={}", input);
		}
		GuestbookDto output = guestbookDsi.selectGuestbook(input.get(0));
		//비즈니스 처리


		return output;
	}
	/**
	 * 신규 Guestbook을 입력한다.
	 *
	 * @param guestbook 방명록 입력정보
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar003c")
	@ServiceName("신규 Guestbook을 입력")
	//@Transactional(timeout = 100 )
	public int insertGuestbook( GuestbookDto guestbook ) {
		int count = guestbookDsi.insertGuestbook(guestbook);

		//try {
		//	Thread.sleep(guestbook.getTimeout());
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
//
		//doTestJpaTransaction(false);
log.warn("============cnt={}", count);
		return count ;
	}

	/**
	 *  Guestbook을 삭제한다
	 *
	 * @param guestbook 방명록 key
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar004d")
	@ServiceName("메세지 삭제")
	public int deleteGuestbook( GuestbookDto guestbook ) {
		if(StringUtils.isEmpty(guestbook.getNo())){
			throw new HlicpException("FWKE00001");
		}
		int count = guestbookDsi.deleteGuestbook(guestbook);
		return count ;
	}
	/**
	 *  Guestbook을 삭제한다
	 *
	 * @param guestbook 방명록 key
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar004u")
	@ServiceName("메세지 삭제")
	public int updateGuestbook( GuestbookDto guestbook ) {
		int count = guestbookDsi.updateGuestbook(guestbook);
		return count ;
	}

	/**
	 * 단건 조회 - 프로시저 호출
	 *
	 * <pre>
	   create or replace PROCEDURE test_proc_value
		(
		p_cd IN OUT VARCHAR2,
		p_name OUT VARCHAR2 )
		IS
		BEGIN

		    SELECT cd_nm into p_name FROM code
		    WHERE cd =p_cd
		    ;
		    p_cd := 'call test_proc....';
		END;
		</pre>
	 * @return 코드명
	 */

	@Override
	@ServiceId("exmsalevar005r")
	@ServiceName("단건 조회 - 프로시저 호출")
	public String callSelectCode(){
		HashMap<String,String> input = new HashMap<String,String>();
		input.put("cd", "ds");
		guestbookDsi.callSelectCode(input);
		if(log.isDebugEnabled()) {
			log.debug("##프로시저 호출결과 ==={}", input.get("result"));
		}
		return input.get("result");
	}

	/**
	 * 	 다건 조회 - 프로시저 호출
	 *
	 * <pre>
	 * create or replace PROCEDURE test_proc
		(
		p_cd IN OUT VARCHAR2,
		p_rc OUT SYS_REFCURSOR )
		IS
		BEGIN
		OPEN p_rc FOR
		    SELECT * FROM code
		    WHERE cd =p_cd
		    ;
		    p_cd := 'call test_proc....';
		END;
		</pre>
	   * @return 코드리스트
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@ServiceId("exmsalevar006r")
	@ServiceName("다건 프로시저테스트")
	public List<HashMap> callSelectCodeList(){
		HashMap<String,Object> input = new HashMap<String,Object>();
		input.put("cd", "ds");
		guestbookDsi.callSelectCodeList(input);
		if(log.isDebugEnabled()) {
			log.debug("##프로시저 호출 결과 ==={}", input.get("resultList"));
		}
		return (List)input.get("resultList");
	}

	/**
	 * 입력 프로시저 거래
	 *
	 *  <pre>
	 * create or replace PROCEDURE TEST_insert_PROC
		(
		    pi_cd IN varchar2,
		    pi_type in varchar2,
		    pi_nm in varchar2,
		    po_VALUE OUT varchar2
		)
		IS
		BEGIN

		    insert into CODE(mstr_cd, cd_type, cd, cd_nm)
		    values ('ds', pi_type, pi_cd, pi_nm);
		    po_VALUE := 'call test_insert_proc';
		END ;
		</pre>

	 *	@return 입력 건수
	 */
	@Override
	@ServiceId("exmsalevar007c")
	@ServiceName("프로시저입력")
	@Transactional
	public String callInsertCode(){
		HashMap<String,String> input = new HashMap<String,String>();
		input.put("cd", "ds2");
		input.put("type", "2");
		input.put("name", "ds2-name");
		guestbookDsi.callInsertCode(input);
		if(log.isDebugEnabled()) {
			log.info("##입력프로시저 테스트 =={}", input.get("result"));
		}
		return input.get("result");
	}

	/**
	 * 후행 처리 샘플임
	 */
	@Override
	@ServiceId(value="exmsalevar008r", applyGlobalInterceptors=false)
	@ServiceName("hazelcast test")
	public String hazelcastTest(GuestbookDto guestbook){
		deferredServiceTemplate.send("exmsalevar002r", guestbook);
    	return "success";
	}

	/**
	 * jpa 트랜잭션 테스트
	 * @param exceptionMode
	 */
	@SuppressWarnings("unused")
	private void doTestJpaTransaction(boolean exceptionMode) {
		/////////
		memberRepositoryDsi.save(new Member("a", RandomUtils.nextInt()));
		//memberRepositoryDsi.save(new Member("b", RandomUtils.nextInt()));
		//memberRepositoryDsi.save(new Member("c", RandomUtils.nextInt()));
		//memberRepositoryDsi.save(new Member("a", RandomUtils.nextInt()));
		if (exceptionMode)
			throw new HlicpException("FWKE00001", "트랜잭션 테스트 =======");

//		Iterable<Member> list1 = memberRepositoryDsi.findAll();
//
//		if(log.isDebugEnabled()) {
//			log.debug("findAll() Method.");
//		}
//		for (Member m : list1) {
//			log.info(m.toString());
//		}
//
//		log.info("findByNameAndAgeLessThan() Method.");
		log.info("=======1111111===========");
		List<Member> list2 = memberRepositoryDsi.findByNameAndAgeLessThan("a", 10);
		for (Member m : list2) {
			log.info(m.toString());
		}
		log.info("=======22222===========");

	}

	/**
	 * 신규 Guestbook을 입력한다.
	 *
	 * @param guestbook 방명록 입력정보
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar009c")
	@ServiceName("신규 Guestbook을 입력")
	public int insertGuestbook2( GuestbookWrapDto guestbook ) {
		int count = guestbookDsi.insertGuestbook2(guestbook);
		if(log.isInfoEnabled()) {
			log.info("before jpa calll,count:={}",  count);
		}

		return count ;
	}

	/**
	 * 신규 Guestbook을 입력한다.
	 *
	 * @param guestbook 방명록 입력정보
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar010c")
	@ServiceName("Rest 트랜잭션 테스트")
	@Transactional
	public GuestbookDto insertGuestbook010( GuestbookDto guestbook ) {
		int count = guestbookDsi.insertGuestbook(guestbook);
		if(log.isInfoEnabled()) {
			log.info("=======3================before jpa calll,count======={}",  count);
		}
		TargetServiceInfo serviceInfo = new TargetServiceInfo("mci","/hlicp/exm/exmsalevar011c");
		serviceInfo.setRestTransactionMode(RestTransactionMode.Yes);
		GuestbookDto aa = client.sendAndReceive(serviceInfo, guestbook, GuestbookDto.class);
		if(log.isInfoEnabled()) {
			log.info("aa={}",  aa);
		}
		String rollback = guestbook.getRollback();
		if("3".equals(rollback)) {
			try {
				Thread.sleep(30000);//pod crash 테스트용
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*TargetServiceInfo serviceInfo2 = new TargetServiceInfo("esb","/hlicp/exm/exmsalevar011c");
		GuestbookDto bb = RestApiClient.sendAndReceive(serviceInfo2, guestbook, GuestbookDto.class);
		if(log.isInfoEnabled()) {
			log.info("bbbb={}",  bb);
		}
		*/

		return guestbook ;
	}

	/**
	 * 신규 Guestbook을 입력한다.
	 *
	 * @param guestbook 방명록 입력정보
	 * @return 처리된 건수
	 */
	@Override
	@ServiceId("exmsalevar011c")
	@ServiceName("Rest 트랜잭션 테스트")
	public GuestbookDto insertGuestbook011( GuestbookDto guestbook ) {
		int count = guestbookDsi.insertGuestbook(guestbook);
		if(log.isInfoEnabled()) {
			log.info("==================3========before jpa calll,count:={}",  count);
		}
		String rollback = guestbook.getRollback();
		String depth = guestbook.getDepth();
		if("1".equals(rollback)) {
			throw new HlicpException("FWKE00002");
		}
		else if("2".equals(rollback)) {
			try {
				Thread.sleep(30000);//pod crash 테스트용
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if("4".equals(rollback)) {
			int iDepth = Integer.valueOf(depth)-1;
			if(log.isInfoEnabled()) {
				log.info("##depth======================={}, iDepth======{}", depth, iDepth);
			}
			if(iDepth > 0) {
				guestbook.setDepth( "" + iDepth);
				TargetServiceInfo serviceInfo = new TargetServiceInfo("esb","/hlicp/exm/exmsalevar013c");
				serviceInfo.setRestTransactionMode(RestTransactionMode.Yes);
				GuestbookDto aa = client.sendAndReceive(serviceInfo, guestbook, GuestbookDto.class);
				if(log.isInfoEnabled()) {
					log.info("##ccc======================={}",  aa);
				}
			}
		}

		return guestbook ;
	}

	@Override
	@ServiceId("exmsalevar012c")
	@ServiceName("그리드 CUD 테스트")
	@Transactional //(readOnly = true)
	public int makeGuestbook(GuestbookListDto guestbookListDto) {
		int cudCounter[] = new int[3];
		// status 가 null이면 예외발생
		for(GuestbookDto guestbookDto: guestbookListDto.getGuestbooks()) {
			if(guestbookDto.getStatus().equals("C")) {
				cudCounter[0] += insertGuestbook013(guestbookDto);
			} else if(guestbookDto.getStatus().equals("U")) {
				cudCounter[1] += updateGuestbook013(guestbookDto);
			} else if(guestbookDto.getStatus().equals("D")) {
//				throw new HlicpException("FWKE00001");
				cudCounter[2] += deleteGuestbook013(guestbookDto);
			}
		};
		log.debug("추가 {} 건, 수정 {} 건 , 삭제 {} 건 ",cudCounter[0],cudCounter[1],cudCounter[2]);
		return 0;
	}
	private int insertGuestbook013(GuestbookDto guestbook) {
		return guestbookDsi.insertGuestbook(guestbook);
	}
	private int updateGuestbook013(GuestbookDto guestbook) {
		return guestbookDsi.updateGuestbook(guestbook);
	}
	private int deleteGuestbook013(GuestbookDto guestbook) {
		return guestbookDsi.deleteGuestbook(guestbook);
	}

	@Override
	@ServiceId("exmsalevar013c")
	@ServiceName("트랜잭션 컨트롤")
	public GuestbookDto transactionControl( GuestbookDto guestbook ) {
		int count = 0;


		DefaultTransactionDefinition td = new DefaultTransactionDefinition();
		td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//		td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		td.setTimeout(300);
		PlatformTransactionManager tm = BomTransactionManagerHolder.getTransactionManager();
		// 테스트 시나리오
		// 1, 2 번 루프까지 한번에 커밋, 3, 4번 루프 정상 롤백 확인
		boolean commitAll = false;
		int index = 1;
		TransactionStatus ts = null;
		for(;index <=4; index++) {
			if(ts == null || ts.isCompleted()) {
				ts = tm.getTransaction(td);
			}
			guestbook.setNo(Long.valueOf(String.valueOf(index + 1)));
			guestbookDsi.insertGuestbook(guestbook);

			//4번째 루프에서 커밋 안하고 종료
			if(index == 4) {
				break;
			}
			if(index%2 == 0) {
				tm.commit(ts);
			}
		}

		if(commitAll) {
			tm.commit(ts);
		} else {
			tm.rollback(ts);
		}

		// Main transaction
		guestbook.setNo(20l);
		guestbookDsi.insertGuestbook(guestbook);

		String rollback = guestbook.getRollback();
		if("1".equals(rollback)) {
			try {
				throw new HlicpException("FWKE00002");
			} catch (Exception e) {

			}
		}

		return guestbook ;
	}

	/**
	 *  @param : guestbook.rollback : 롤백 유형 0, 1, 2, 3
	 *  @param : guestbook.status : 독립 트랜잭션 내 호출 서비스 유형(PSI or CSI else null)
	 */
	@Override
	@ServiceId("exmsalevar014c")
	@ServiceName("독립 트랜잭션 ")
	public GuestbookDto transactionControl2(GuestbookDto guestbook) {
		String rollback = guestbook.getRollback();
		String state = guestbook.getStatus();


		// Main transaction
		if(!"3".equals(rollback)) {
			guestbook.setNo(1l);
			guestbookDsi.insertGuestbook(guestbook);
		}

		HlicpTransactionManagerHolder.executeRequiresNew(status -> {
			int returnValue = 0 ;
			//1. 일반 커밋  rollback state == 0
			// 별도의 커밋 명령 없어도 커밋 됨
			//A. 타 CSI 호출
			if(state != null && state.equals("CSI")) {
				CodeDto code = new CodeDto();
				code.setCd("ds");
				code.setCdNm("test");
				code.setMstrCd("11");
				code.setCdType("ds");
				returnValue = codeMangCsi.insertCode(code);

			//B. 자체 PSI 호출
			} else if(state != null && state.equals("PSI")){
				guestbook.setNo(5L);
				// private 메소드 호출
				returnValue = this.insertGuestbook013(guestbook);

			}

			//2. 예외 발생  rollback state == 1
			// 에러 발생시 null 리턴, 메인트랜잭션만 커밋
			if("1".equals(rollback)) {
				throw new HlicpException("FWKE00002");
			}

			//3. 명시적 롤백 rollback state == 2
			// 정상 작동, 메인트랜잭션만 커밋
			if("2".equals(rollback)) {
				status.setRollbackOnly();
			}

			return null;
		});


		// 4. 메인 트랜잭션 에러시
		// 독립 트랜잭션에 영향 없음
		if("3".equals(rollback)) {
			// Main transaction
			guestbook.setNo(1l);
			guestbookDsi.insertGuestbook(guestbook);

			throw new HlicpException("FWKE00002");

		}



		return guestbook ;
	}

//	@Override
//	@ServiceId("exmsalevar015c")
//	@ServiceName("온디맨드 배치 호출 ")
//	public void callBatch( GuestbookDto guestbook ) {
//		onBatchCaller.execute("exmSaleVar04JOB", "");
//	}
}
