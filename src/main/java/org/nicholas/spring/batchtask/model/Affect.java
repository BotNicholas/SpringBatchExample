package org.nicholas.spring.batchtask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "affects")
public class Affect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "direction_id", referencedColumnName = "id")
    @NotNull
    private Direction direction;
    @Min(1990)
    private Integer year;
    @NotNull
    private Date date;
    @NotEmpty
    private String weekday;
    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    @NotNull
    private Country country;
    @NotEmpty
    private String commodity;
    @Column(name = "transport_mode")
    @NotEmpty
    private String transportMode;
    @NotEmpty
    private String measure;
    @Min(0)
    private Long value;
    @Min(0)
    private Long cumulative;

    public Affect() {
    }

    public Affect(Direction direction, Integer year, Date date, String weekday, Country country, String commodity, String transportMode, String measure, Long value, Long cumulative) {
        this.direction = direction;
        this.year = year;
        this.date = date;
        this.weekday = weekday;
        this.country = country;
        this.commodity = commodity;
        this.transportMode = transportMode;
        this.measure = measure;
        this.value = value;
        this.cumulative = cumulative;
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getCumulative() {
        return cumulative;
    }

    public void setCumulative(Long cumulative) {
        this.cumulative = cumulative;
    }

    @Override
    public String toString() {
        return "Affect{" +
                "direction=" + direction +
                ", year=" + year +
                ", date=" + date +
                ", weekday='" + weekday + '\'' +
                ", country=" + country +
                ", commodity='" + commodity + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", measure='" + measure + '\'' +
                ", value=" + value +
                ", cumulative=" + cumulative +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affect affect = (Affect) o;
        return Objects.equals(direction, affect.direction) && Objects.equals(year, affect.year) && Objects.equals(date, affect.date) && Objects.equals(weekday, affect.weekday) && Objects.equals(country, affect.country) && Objects.equals(commodity, affect.commodity) && Objects.equals(transportMode, affect.transportMode) && Objects.equals(measure, affect.measure) && Objects.equals(value, affect.value) && Objects.equals(cumulative, affect.cumulative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, year, date, weekday, country, commodity, transportMode, measure, value, cumulative);
    }
}
