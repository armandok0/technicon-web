package gr.technico.technikon.services;

import gr.technico.technikon.exceptions.CustomException;
import gr.technico.technikon.model.Owner;
import gr.technico.technikon.repositories.OwnerRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OwnerServiceTest {

    @Inject
    private OwnerRepository ownerRepository;

    private OwnerServiceImpl ownerServiceImpl;

    private final String vat = "100000000";
    private final String name = "TestName";
    private final String surname = "TestSurname";
    private final String address = "Test an Adress";
    private final String phoneNumber = "6999999999";
    private final String email = "testemail@example.com";
    private final String username = "testusername";
    private final String password = "testpassword";

    @BeforeEach
    public void setUp() throws CustomException {
        ownerRepository = mock(OwnerRepository.class);
        ownerServiceImpl = new OwnerServiceImpl(ownerRepository);
        Owner mockOwner = new Owner(1L, vat, name, surname, address, phoneNumber, email, username, password, false, null, null);
        when(ownerRepository.findByVat(vat)).thenReturn(Optional.of(mockOwner));
    }

    @Test
    public void testCreateOwner() throws CustomException {
        Owner owner = ownerRepository.findByVat(vat).orElse(null);
        assertNotNull(owner);
        assertEquals(name, owner.getName());
        assertEquals(surname, owner.getSurname());
        assertEquals(address, owner.getAddress());
        assertEquals(phoneNumber, owner.getPhoneNumber());
        assertEquals(email, owner.getEmail());
        assertEquals(username, owner.getUsername());
        assertEquals(password, owner.getPassword());
    }

    @Test
    public void testSearchOwnerByVat() throws CustomException {
        Optional<Owner> foundOwner = ownerServiceImpl.searchOwnerByVat(vat);
        assertTrue(foundOwner.isPresent());
        assertEquals(vat, foundOwner.get().getVat());
        assertEquals(name, foundOwner.get().getName());
    }

    @Test
    public void testSearchOwnerByEmail() throws CustomException {
        Owner mockOwner = new Owner();
        mockOwner.setEmail(email);
        mockOwner.setName(name);
        when(ownerRepository.findByEmail(email)).thenReturn(Optional.of(mockOwner));
        Optional<Owner> foundOwner = ownerServiceImpl.searchOwnerByEmail(email);
        assertTrue(foundOwner.isPresent());
        assertEquals(email, foundOwner.get().getEmail());
        assertEquals(name, foundOwner.get().getName());
        verify(ownerRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testUpdateOwnerAddress() throws CustomException {
        String newAddress = "New Address";
        ownerServiceImpl.updateOwnerAddress(vat, newAddress);
        Owner owner = ownerRepository.findByVat(vat).orElse(null);
        assertNotNull(owner);
        assertEquals(newAddress, owner.getAddress());
    }

    @Test
    public void testUpdateOwnerEmail() throws CustomException {
        String newEmail = "new.email@gmail.com";
        ownerServiceImpl.updateOwnerEmail(vat, newEmail);
        Owner owner = ownerRepository.findByVat(vat).orElse(null);
        assertNotNull(owner);
        assertEquals(newEmail, owner.getEmail());
    }

    @Test
    public void testUpdateOwnerPassword() throws CustomException {
        String newPassword = "newpassword";
        ownerServiceImpl.updateOwnerPassword(vat, newPassword);
        Owner owner = ownerRepository.findByVat(vat).orElse(null);
        assertNotNull(owner);
        assertEquals(newPassword, owner.getPassword());
    }

    @Test
    public void testDeleteOwnerPermanently() throws CustomException {
        when(ownerRepository.deletePermanentlyByVat(vat)).thenReturn(true);
        when(ownerRepository.findByVat(vat)).thenReturn(Optional.empty());
        boolean deleted = ownerServiceImpl.deleteOwnerPermanently(vat);
        assertTrue(deleted);
        Optional<Owner> foundOwner = ownerServiceImpl.searchOwnerByVat(vat);
        assertFalse(foundOwner.isPresent());
        verify(ownerRepository, times(1)).deletePermanentlyByVat(vat);
    }

    @Test
    public void testDeleteOwnerSafely() throws CustomException {
        boolean deleted = ownerServiceImpl.deleteOwnerSafely(vat);
        assertTrue(deleted);
        Owner owner = ownerRepository.findByVat(vat).orElse(null);
        assertNotNull(owner);
        assertTrue(owner.isDeleted());
    }

    @Test
    public void testValidateVat() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validateVat("123"));
        assertDoesNotThrow(() -> ownerServiceImpl.validateVat("123456789"));
    }

    @Test
    public void testValidateName() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validateName(""));
        assertDoesNotThrow(() -> ownerServiceImpl.validateName("Nikos"));
    }

    @Test
    public void testValidateSurname() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validateSurname(""));
        assertDoesNotThrow(() -> ownerServiceImpl.validateSurname("Aygoustakis"));
    }

    @Test
    public void testValidatePassword() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validatePassword("123"));
        assertDoesNotThrow(() -> ownerServiceImpl.validatePassword("12345678"));
    }

    @Test
    public void testValidatePhone() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validatePhone("123e45"));
        assertThrows(CustomException.class, () -> ownerServiceImpl.validatePhone("123334456766656766"));
        assertDoesNotThrow(() -> ownerServiceImpl.validatePhone("69996886775"));
    }

    @Test
    public void testValidateEmail() {
        assertThrows(CustomException.class, () -> ownerServiceImpl.validateEmail("email"));
        assertDoesNotThrow(() -> ownerServiceImpl.validateEmail("validemail@example.com"));
    }

    @Test
    public void testCheckVat() throws CustomException {
        assertThrows(CustomException.class, () -> ownerServiceImpl.checkVat(vat));
    }

    @Test
    public void testCheckEmail() throws CustomException {
        when(ownerRepository.findByEmail(email)).thenReturn(Optional.of(new Owner()));
        assertThrows(CustomException.class, () -> ownerServiceImpl.checkEmail(email));
        verify(ownerRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testCheckUsername() throws CustomException {
        assertThrows(CustomException.class, () -> ownerServiceImpl.checkVat(vat));
    }
}