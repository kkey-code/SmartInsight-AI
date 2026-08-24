//package com.wkr.document.service.impl;
//
//import com.wkr.core.exception.BusinessException;
//import com.wkr.document.dto.DocumentCreateDTO;
//import com.wkr.document.entity.Document;
//import com.wkr.document.enums.DocumentStatus;
//import com.wkr.document.mapper.DocumentMapper;
//import com.wkr.document.service.FileStorage;
//import com.wkr.document.vo.DocumentDownloadVO;
//import com.wkr.document.vo.DocumentVO;
//import com.wkr.web.context.UserContext;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DocumentServiceImplTest {
//
//    @Mock
//    private FileStorage fileStorage;
//
//    @Mock
//    private DocumentMapper documentMapper;
//
//    private DocumentServiceImpl documentService;
//
//    private MockedStatic<UserContext> userContextMock;
//
//    @BeforeEach
//    void setUp() {
//
//        documentService = new DocumentServiceImpl(fileStorage);
//
//        ReflectionTestUtils.setField(
//                documentService,
//                "baseMapper",
//                documentMapper
//        );
//
//        userContextMock =
//                mockStatic(UserContext.class);
//
//        userContextMock
//                .when(UserContext::getUserId)
//                .thenReturn(1L);
//
//        userContextMock
//                .when(UserContext::isAdmin)
//                .thenReturn(false);
//    }
//
//    @AfterEach
//    void tearDown() {
//        userContextMock.close();
//    }
//
//    @Test
//    void create_shouldCreateDocument() {
//
//        DocumentCreateDTO dto = new DocumentCreateDTO();
//        dto.setTitle("测试文档");
//        dto.setDescription("测试描述");
//
//        doAnswer(invocation -> {
//            Document document = invocation.getArgument(0);
//            document.setId(100L);
//            return 1;
//        }).when(documentMapper)
//                .insert(any(Document.class));
//
//        DocumentVO result =
//                documentService.create(dto);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//        assertEquals(1L, result.getOwnerId());
//        assertEquals("测试文档", result.getTitle());
//        assertEquals("测试描述", result.getDescription());
//        assertEquals(
//                DocumentStatus.DRAFT.getCode(),
//                result.getStatus()
//        );
//
//        verify(documentMapper)
//                .insert(any(Document.class));
//    }
//
//    @Test
//    void create_shouldRejectWhenNotLogin() {
//
//        userContextMock
//                .when(UserContext::getUserId)
//                .thenReturn(null);
//
//        DocumentCreateDTO dto = new DocumentCreateDTO();
//        dto.setTitle("测试文档");
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.create(dto)
//                );
//
//        assertEquals(401, exception.getCode());
//        assertEquals("未登录", exception.getMessage());
//
//        verify(documentMapper, never())
//                .insert(any(Document.class));
//    }
//
//    @Test
//    void getDetail_shouldReturnOwnDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "我的文档"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        DocumentVO result =
//                documentService.getDetail(100L);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//        assertEquals("我的文档", result.getTitle());
//    }
//
//    @Test
//    void getDetail_shouldReturn404WhenNotFound() {
//
//        when(documentMapper.selectById(999L))
//                .thenReturn(null);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.getDetail(999L)
//                );
//
//        assertEquals(404, exception.getCode());
//        assertEquals("文档不存在", exception.getMessage());
//    }
//
//    @Test
//    void getDetail_shouldRejectOtherUserDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        2L,
//                        "别人的文档"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.getDetail(100L)
//                );
//
//        assertEquals(403, exception.getCode());
//        assertEquals("无权操作该文档", exception.getMessage());
//    }
//
//    @Test
//    void getDetail_shouldAllowAdmin() {
//
//        userContextMock
//                .when(UserContext::isAdmin)
//                .thenReturn(true);
//
//        Document document =
//                createDocument(
//                        100L,
//                        2L,
//                        "别人的文档"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        DocumentVO result =
//                documentService.getDetail(100L);
//
//        assertNotNull(result);
//        assertEquals("别人的文档", result.getTitle());
//    }
//
//    @Test
//    void update_shouldUpdateOwnDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "旧标题"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        com.wkr.document.dto.DocumentUpdateDTO dto =
//                new com.wkr.document.dto.DocumentUpdateDTO();
//
//        dto.setId(100L);
//        dto.setTitle("新标题");
//        dto.setDescription("新描述");
//
//        DocumentVO result =
//                documentService.update(dto);
//
//        assertEquals("新标题", result.getTitle());
//        assertEquals("新描述", result.getDescription());
//
//        verify(documentMapper)
//                .updateById(document);
//    }
//
//    @Test
//    void update_shouldRejectOtherUserDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        2L,
//                        "别人"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        com.wkr.document.dto.DocumentUpdateDTO dto =
//                new com.wkr.document.dto.DocumentUpdateDTO();
//
//        dto.setId(100L);
//        dto.setTitle("修改");
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.update(dto)
//                );
//
//        assertEquals(403, exception.getCode());
//
//        verify(documentMapper, never())
//                .updateById(any(Document.class));
//    }
//
//    @Test
//    void delete_shouldDeleteOwnDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "测试"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        documentService.delete(100L);
//
//        verify(documentMapper)
//                .deleteById(100L);
//
//        verify(fileStorage, never())
//                .delete(anyString());
//    }
//
//    @Test
//    void delete_shouldDeleteStorageFile() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "测试"
//                );
//
//        document.setStorageKey(
//                "1/100/test.pdf"
//        );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        documentService.delete(100L);
//
//        verify(documentMapper)
//                .deleteById(100L);
//
//        verify(fileStorage)
//                .delete("1/100/test.pdf");
//    }
//
//    @Test
//    void delete_shouldRejectOtherUserDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        2L,
//                        "别人"
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.delete(100L)
//                );
//
//        assertEquals(403, exception.getCode());
//
//        verify(documentMapper, never())
//                .deleteById(anyLong());
//
//        verify(fileStorage, never())
//                .delete(anyString());
//    }
//
//    @Test
//    void upload_shouldRejectEmptyFile() {
//
//        MockMultipartFile file =
//                new MockMultipartFile(
//                        "file",
//                        "test.pdf",
//                        "application/pdf",
//                        new byte[0]
//                );
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.upload(file)
//                );
//
//        assertEquals(400, exception.getCode());
//        assertEquals(
//                "上传文件不能为空",
//                exception.getMessage()
//        );
//    }
//
//    @Test
//    void upload_shouldRejectInvalidFilename() {
//
//        MockMultipartFile file =
//                new MockMultipartFile(
//                        "file",
//                        "..",
//                        "application/pdf",
//                        "test".getBytes()
//                );
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.upload(file)
//                );
//
//        assertEquals(400, exception.getCode());
//        assertEquals(
//                "非法文件名",
//                exception.getMessage()
//        );
//    }
//
//    @Test
//    void upload_shouldUploadSuccessfully() {
//
//        MockMultipartFile file =
//                new MockMultipartFile(
//                        "file",
//                        "测试.pdf",
//                        "application/pdf",
//                        "hello".getBytes()
//                );
//
//        doAnswer(invocation -> {
//            Document document =
//                    invocation.getArgument(0);
//
//            document.setId(100L);
//            return 1;
//        }).when(documentMapper)
//                .insert(any(Document.class));
//
//        when(fileStorage.upload(
//                eq(file),
//                anyString()
//        )).thenReturn(
//                "1/100/test.pdf"
//        );
//
//        DocumentVO result =
//                documentService.upload(file);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//        assertEquals(1L, result.getOwnerId());
//        assertEquals(
//                "测试.pdf",
//                result.getFileName()
//        );
//        assertEquals(
//                "application/pdf",
//                result.getFileType()
//        );
//        assertEquals(
//                DocumentStatus.READY.getCode(),
//                result.getStatus()
//        );
//        assertEquals(
//                "1/100/test.pdf",
//                result.getStorageKey()
//        );
//
//        verify(documentMapper)
//                .insert(any(Document.class));
//
//        verify(fileStorage)
//                .upload(eq(file), anyString());
//
//        verify(documentMapper)
//                .updateById(any(Document.class));
//    }
//
//    @Test
//    void upload_shouldMarkFailedWhenStorageUploadFails() {
//
//        MockMultipartFile file =
//                new MockMultipartFile(
//                        "file",
//                        "test.pdf",
//                        "application/pdf",
//                        "hello".getBytes()
//                );
//
//        doAnswer(invocation -> {
//            Document document =
//                    invocation.getArgument(0);
//
//            document.setId(100L);
//            return 1;
//        }).when(documentMapper)
//                .insert(any(Document.class));
//
//        when(fileStorage.upload(
//                eq(file),
//                anyString()
//        )).thenThrow(
//                new RuntimeException("disk error")
//        );
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.upload(file)
//                );
//
//        assertEquals(500, exception.getCode());
//        assertEquals(
//                "文件上传失败",
//                exception.getMessage()
//        );
//
//        verify(documentMapper)
//                .insert(any(Document.class));
//
//        verify(documentMapper)
//                .updateById(
//                        argThat((Document document) ->
//                                document.getId().equals(100L)
//                                        && document.getStatus()
//                                        .equals(DocumentStatus.FAILED.getCode())
//                                        && "文件上传失败".equals(
//                                        document.getErrorMessage()
//                                )
//                        )
//                );
//    }
//
//    @Test
//    void download_shouldReturnDownloadVO() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "测试"
//                );
//
//        document.setFileName("测试文档.pdf");
//        document.setFileType("application/pdf");
//        document.setStorageKey(
//                "1/100/test.pdf"
//        );
//        document.setStatus(
//                DocumentStatus.READY.getCode()
//        );
//
//        Resource resource =
//                new ByteArrayResource(
//                        "hello".getBytes()
//                );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        when(fileStorage.load(
//                "1/100/test.pdf"
//        )).thenReturn(resource);
//
//        DocumentDownloadVO result =
//                documentService.download(100L);
//
//        assertNotNull(result);
//
//        assertSame(
//                resource,
//                result.getResource()
//        );
//
//        assertEquals(
//                "测试文档.pdf",
//                result.getFileName()
//        );
//
//        assertEquals(
//                "application/pdf",
//                result.getFileType()
//        );
//
//        verify(fileStorage)
//                .load("1/100/test.pdf");
//    }
//
//    @Test
//    void download_shouldReturn404WhenNotFound() {
//
//        when(documentMapper.selectById(999L))
//                .thenReturn(null);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.download(999L)
//                );
//
//        assertEquals(404, exception.getCode());
//    }
//
//    @Test
//    void download_shouldRejectOtherUserDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        2L,
//                        "别人"
//                );
//
//        document.setStorageKey(
//                "2/100/test.pdf"
//        );
//
//        document.setStatus(
//                DocumentStatus.READY.getCode()
//        );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.download(100L)
//                );
//
//        assertEquals(403, exception.getCode());
//
//        verify(fileStorage, never())
//                .load(anyString());
//    }
//
//    @Test
//    void download_shouldRejectNonReadyDocument() {
//
//        Document document =
//                createDocument(
//                        100L,
//                        1L,
//                        "处理中"
//                );
//
//        document.setStorageKey(
//                "1/100/test.pdf"
//        );
//
//        document.setStatus(
//                DocumentStatus.PROCESSING.getCode()
//        );
//
//        when(documentMapper.selectById(100L))
//                .thenReturn(document);
//
//        BusinessException exception =
//                assertThrows(
//                        BusinessException.class,
//                        () -> documentService.download(100L)
//                );
//
//        assertEquals(400, exception.getCode());
//
//        assertEquals(
//                "文档当前不可下载",
//                exception.getMessage()
//        );
//
//        verify(fileStorage, never())
//                .load(anyString());
//    }
//
//    private Document createDocument(
//            Long id,
//            Long ownerId,
//            String title
//    ) {
//
//        Document document = new Document();
//
//        document.setId(id);
//        document.setOwnerId(ownerId);
//        document.setTitle(title);
//        document.setStatus(
//                DocumentStatus.DRAFT.getCode()
//        );
//
//        return document;
//    }
//}