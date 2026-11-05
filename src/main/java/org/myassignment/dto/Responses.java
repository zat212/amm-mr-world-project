//package org.myassignment.dto;
//
//import org.myassignment.entity.DepartureStatus;
//import org.myassignment.entity.OptionCategory;
//import org.myassignment.entity.PackageStatus;
//import org.myassignment.entity.ReservationStatus;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
///** What the API sends back. Entities are never returned directly. */
//public final class Responses {
//
//    private Responses() {}
//
//    public record ThemeResponse(Long id, String name) {}
//
//    public record TierResponse(Long id, String name) {}
//
//    public record UserResponse(Long id, String name, String email, String phone, String role) {}
//
//    public record OptionResponse(Long id, OptionCategory category, String name, BigDecimal extraPrice) {}
//
//    public record ItemResponse(Long id, String name, String category, BigDecimal price, Integer stockQty) {}
//
//    public record PackageItemResponse(Long itemId, String name, Integer quantity) {}
//
//    public record DepartureResponse(
//            Long id,
//            Long packageId,
//            LocalDate departureDate,
//            Integer minApplicants,
//            DepartureStatus status
//            // current total, so the UI can show "2 / 3 needed"
//    ) {}
//
//    public record PackageSummaryResponse(
//            Long id,
//            String title,
//            String description,
//            BigDecimal basePrice,
//            Integer durationDays,
//            PackageStatus status,
//            Long themeId,
//            String themeName,
//            Long tierId,
//            String tierName
//    ) {}
//
//    public record PackageDetailResponse(
//            Long id,
//            String title,
//            String description,
//            BigDecimal basePrice,
//            Integer durationDays,
//            PackageStatus status,
//            Long themeId,
//            String themeName,
//            Long tierId,
//            String tierName,
//            List<OptionResponse> options,
//            List<DepartureResponse> departures,
//            List<PackageItemResponse> items
//    ) {}
//
//    public record ReservationResponse(
//            Long id,
//            Long userId,
//            String userName,
//            String userEmail,
//            Long packageId,
//            String packageTitle,
//            Long departureId,
//            LocalDate departureDate,
//            Integer applicantsCount,
//            BigDecimal totalPrice,
//            ReservationStatus status,
//            LocalDateTime reservedAt,
//            List<OptionResponse> options
//    ) {}
//}
