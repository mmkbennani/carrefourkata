package com.carrefour.domain.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * UniqueError
 */

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("uniqueError")
public class UniqueError {

    @JsonProperty("type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("details")
    private String details;

    @JsonProperty("instance")
    private String instance;

    public UniqueError type(String type) {
        this.type = type;
        return this;
    }

    /**
     * this technical code is used to identify the error type, useful for automated testing
     *
     * @return type
     */
    @NotNull
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UniqueError title(String title) {
        this.title = title;
        return this;
    }

    /**
     * short description of the error in clear text
     *
     * @return title
     */
    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UniqueError details(String details) {
        this.details = details;
        return this;
    }

    /**
     * optional clear text describing the detail of the error
     *
     * @return details
     */

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public UniqueError instance(String instance) {
        this.instance = instance;
        return this;
    }

    /**
     * optional path of the resource linked to the error
     *
     * @return instance
     */

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UniqueError uniqueError = (UniqueError) o;
        return Objects.equals(this.type, uniqueError.type) &&
                Objects.equals(this.title, uniqueError.title) &&
                Objects.equals(this.details, uniqueError.details) &&
                Objects.equals(this.instance, uniqueError.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, details, instance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UniqueError {\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    details: ").append(toIndentedString(details)).append("\n");
        sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

