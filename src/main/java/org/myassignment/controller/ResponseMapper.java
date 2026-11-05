package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.*;

import org.myassignment.entity.CustomizeOption;
import org.myassignment.entity.Departure;
import org.myassignment.entity.PackageItem;
import org.myassignment.entity.Reservation;
import org.myassignment.entity.Theme;
import org.myassignment.entity.Tier;
import org.myassignment.entity.TravelItem;
import org.myassignment.entity.TravelPackage;
import org.myassignment.entity.User;
import org.myassignment.service.ReservationService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Entity -> response conversion.
 * Lazy relations are read here, inside the request, which works with Spring Boot's
 * default spring.jpa.open-in-view=true. If you turn that off, map inside the services instead.
 */
@Component
@RequiredArgsConstructor
public class ResponseMapper {

    private final ReservationService reservationService;

    public ThemeResponse theme(Theme t) {
        return new ThemeResponse(t.getId(), t.getName());
    }

    public TierResponse tier(Tier t) {
        return new TierResponse(t.getId(), t.getName());
    }

    public UserResponse user(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhoneNumber(),
                u.getRole() == null ? null : u.getRole().name());
    }

    public OptionResponse option(CustomizeOption o) {
        return new OptionResponse(o.getId(), o.getCategory(), o.getName(), o.getExtraPrice());
    }

    public ItemResponse item(TravelItem i) {
        return new ItemResponse(i.getId(), i.getName(), i.getCategory(), i.getPrice(), i.getStockQty());
    }

    public PackageItemResponse packageItem(PackageItem pi) {
        return new PackageItemResponse(pi.getItem().getId(), pi.getItem().getName(), pi.getQuantity());
    }

    public DepartureResponse departure(Departure d) {
        return new DepartureResponse(d.getId(), d.getTravelPackage().getId(), d.getDepartureDate(),
                d.getMinApplicants(), d.getStatus());
    }

    public PackageSummaryResponse summary(TravelPackage p) {
        return new PackageSummaryResponse(p.getId(), p.getTitle(), p.getDescription(), p.getBasePrice(),
                p.getDurationDays(), p.getStatus(),
                p.getTheme().getId(), p.getTheme().getName(),
                p.getTier().getId(), p.getTier().getName());
    }

    /** departures are passed in, because customers only see bookable ones and staff see all. */
    public PackageDetailResponse detail(TravelPackage p, List<Departure> departures) {
        return new PackageDetailResponse(p.getId(), p.getTitle(), p.getDescription(), p.getBasePrice(),
                p.getDurationDays(), p.getStatus(),
                p.getTheme().getId(), p.getTheme().getName(),
                p.getTier().getId(), p.getTier().getName(),
                p.getOptions().stream().map(this::option).toList(),
                departures.stream().map(this::departure).toList(),
                p.getPackageItems().stream().map(this::packageItem).toList());
    }

    public ReservationResponse reservation(Reservation r) {
        Departure d = r.getDeparture();
        TravelPackage p = d.getTravelPackage();
        return new ReservationResponse(r.getId(),
                r.getUser().getId(), r.getUser().getFullName(), r.getUser().getEmail(),
                p.getId(), p.getTitle(),
                d.getId(), d.getDepartureDate(),
                r.getApplicantsCount(), r.getTotalPrice(), r.getStatus(), r.getReservedAt(),
                r.getOptions().stream().map(ro -> option(ro.getOption())).toList());
    }
}
