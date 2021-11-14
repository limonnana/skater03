package com.limonnana.skate.web.rest;

import com.limonnana.skate.config.Constants;
import com.limonnana.skate.domain.User;
import com.limonnana.skate.repository.UserRepository;
import com.limonnana.skate.service.UserService;
import com.limonnana.skate.service.dto.PictureDTO;
import com.limonnana.skate.service.dto.UserDTO;
import com.limonnana.skate.web.rest.errors.BadRequestAlertException;
import java.util.*;
import java.util.Collections;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    );

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;

    private final UserRepository userRepository;

    public PublicUserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * {@code GET /users} : get all users with only the public informations - calling this are allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(Pageable pageable) {
        log.debug("REST request to get all public User names");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    @PostMapping("/users/profilepicture")
    public ResponseEntity<User> addProfilePicture(@Valid @RequestBody PictureDTO pictureDTO) {
        User user = userRepository.findOneByLogin(pictureDTO.getLogin().toLowerCase()).get();

        if (user == null) {
            throw new BadRequestAlertException(" user with that login doesn't exist ", "UserNULL", "UserNULL");
        }
        user.setProfilePicture(pictureDTO.getPicture());
        user = userRepository.save(user);

        return com.limonnana.skate.web.rest.ResponseUtil.wrapOrNotFound(
            Optional.of(user),
            HeaderUtil.createAlert(applicationName, "Profile Picture has been updated", user.getLogin())
        );
    }

    @GetMapping("/getuser/{login}")
    public ResponseEntity<User> getUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        log.debug("REST request to get User : {}", login);
        User user = userRepository.findOneByLogin(login).get();
        return com.limonnana.skate.web.rest.ResponseUtil.wrapOrNotFound(
            Optional.of(user),
            HeaderUtil.createAlert(applicationName, "User found with login: ", user.getLogin())
        );
    }
}
