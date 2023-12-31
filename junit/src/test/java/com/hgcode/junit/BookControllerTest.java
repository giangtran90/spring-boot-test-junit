package com.hgcode.junit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
class BookControllerTest {

    // thu 1: tao doi tuong mockMvc
    private MockMvc mockMvc;

    // thu 2: tao trinh anh xa doi tuong: chuyen doi mot json thanh chuoi va nguoc lai
    ObjectMapper objectMapper = new ObjectMapper();

    // thu 3: tao trinh ghi doi tuong (hien tai global ko khuyen nghi nhung du cho du an nay)
    ObjectWriter objectWriter = objectMapper.writer();

    // thu 4: Tao mock de co the thong nhat cho toan bo test
    @Mock
    private BookRepository bookRepository;

    // thu 5: tiem ban mo phong vao
    @InjectMocks
    private BookController bookController;

    // thu 6: tao ca du lieu test
    Book Record_1 = new Book(1L,"Toan","Hoc toan",5);
    Book Record_2 = new Book(2L,"Ly","Hoc ly",4);
    Book Record_3 = new Book(3L,"Hoa","Hoc hoa",3);

    // thu 7: thiet lap trinh dieu khien sach va ta se xay dung tren do
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllBooks_success() throws Exception{
        // b1: Tao list books
        List<Book> books = new ArrayList<>(Arrays.asList(Record_1,Record_2,Record_3));

        // b2: gia lap kho luu tru va gia mao ket qua cua minh bang cach su dung cac ban ghi
        Mockito.when(bookRepository.findAll()).thenReturn(books);

        // b2: dung mockMvc mo phong => tao 1 trinh yeu cau
        mockMvc.perform(
                MockMvcRequestBuilders
                .get("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is("Hoa")))
                .andExpect(jsonPath("$[0].name", is("Toan")));

    }

    @Test
    public void getBookById_Success() throws Exception{
        Mockito.when(bookRepository.findById(Record_1.getId())).thenReturn(java.util.Optional.of(Record_1));
        mockMvc.perform(
                MockMvcRequestBuilders
                .get("/api/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.name", is("Toan")));
    }

    // tao ban ghi => mo phong luu (method save) => khi thuc hien bai dang dam bao o dang chuoi
    @Test
    public void createBook_success() throws Exception {
        // tao ban ghi
        Book record = Book.builder()
                .id(4L)
                .name("Sinh")
                .summary("sach Sinh ")
                .rating(5).build();
        String content = objectWriter.writeValueAsString(record);

        // mo phong phuong thuc luu va tra ket qua
        Mockito.when(bookRepository.save(record)).thenReturn(record);

        // Mo phong kich ban test => dau tien xay dung mot ban dung Servlet gia lap
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);
        // Sau khi co ban yeu cau mo phong de post data => thuc hien yeu cau gia dinh
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.name", is("Sinh")));
    }

    @Test
    public void UpdateBook() throws Exception{
        Book updatedBook = Book.builder()
                .id(1L)
                .name("Anh")
                .summary("Sach Anh")
                .rating(4).build();

        String updatedContent = objectWriter.writeValueAsString(updatedBook);

        Mockito.when(bookRepository.findById(Record_1.getId())).thenReturn(java.util.Optional.ofNullable(Record_1));
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.put("/api/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedContent);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Anh")));
    }

    @Test
    public void deleteBook_success() throws Exception{
        Mockito.when(bookRepository.findById(Record_2.getId())).thenReturn(Optional.of(Record_2));
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/books/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}