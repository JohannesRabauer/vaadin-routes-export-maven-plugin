package dev.rabauer.vaadin.routes.export.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Describes a single Vaadin route detected during static analysis.
 *
 * <p>Serialization note: {@code null} fields are omitted from JSON / YAML output to
 * keep the artifact concise.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteDescriptor {

    /** The route path (empty string represents the root route). */
    private String path;

    /** Fully qualified class name of the navigation target. */
    private String className;

    /**
     * Ordered list of fully qualified layout class names, outermost first.
     * {@code null} when layout resolution is disabled.
     */
    private List<String> layouts;

    /**
     * Roles required to access this route.
     * <ul>
     *   <li>{@code ["ADMIN", "USER"]} – restricted to those roles</li>
     *   <li>{@code ["*"]} – permit-all / anonymous</li>
     *   <li>{@code []} – deny-all</li>
     *   <li>{@code null} – no annotation present</li>
     * </ul>
     */
    private List<String> roles;

    /** Effective access classification. */
    private AccessType access;

    /** Additional route aliases declared with {@code @RouteAlias}. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> aliases;

    /** Origin of the security information. */
    private SecuritySource securitySource;

    /**
     * {@code true} when the route path cannot be fully resolved at analysis time
     * (e.g. programmatic / dynamic registration).
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean dynamic;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RouteDescriptor() {
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getLayouts() {
        return layouts;
    }

    public void setLayouts(List<String> layouts) {
        this.layouts = layouts;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public AccessType getAccess() {
        return access;
    }

    public void setAccess(AccessType access) {
        this.access = access;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public SecuritySource getSecuritySource() {
        return securitySource;
    }

    public void setSecuritySource(SecuritySource securitySource) {
        this.securitySource = securitySource;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
