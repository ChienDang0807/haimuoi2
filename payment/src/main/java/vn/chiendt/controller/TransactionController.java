package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.AdvanceSearchRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
@Slf4j(topic = "TRANSACTION-CONTROLLER")
@Tag(name = "Transaction Controller")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get all transactions", description = "API get all transactions")
    @GetMapping("/list")
    public ApiResponse getTransactionList(@RequestParam(required = false) String search,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        log.info("Get all transactions");

        return ApiResponse.builder()
                .status(200)
                .message("transactions")
                .data(transactionService.getAllTransactions(search, sort, page, size))
                .build();
    }

    @Operation(summary = "Advance search transaction", description = "API for search transaction")
    @PostMapping("/search")
    public ApiResponse advanceSearch(@RequestBody AdvanceSearchRequest request) {
        log.info("Search transaction");

        request.validate();
        PageResponse<?> result = transactionService.advanceSearch(request);

        return ApiResponse.builder()
                .status(200)
                .message("transactions")
                .data(result)
                .build();
    }

    @Operation(summary = "Get transaction detail", description = "API Get transaction detail")
    @GetMapping("/{id}")
    public ApiResponse getTransactionDetail(@PathVariable Long id) {
        log.info("Get transaction detail by id: {}", id);

        return ApiResponse.builder()
                .status(200)
                .message("transaction")
                .data(transactionService.getTransactionDetail(id))
                .build();
    }
}