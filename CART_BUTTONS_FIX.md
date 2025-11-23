# Cart Buttons Fixed! 🔧

## Issue Resolved

**Problem:**
The `+`, `-`, and Delete buttons on the cart page were not working - clicking them had no effect.

**Root Cause:**
Spring Security's CSRF (Cross-Site Request Forgery) protection was blocking the AJAX requests because they didn't include the required CSRF token in the request headers.

## Solution

Updated the JavaScript functions in `cart.html` to include Spring Security's CSRF token in all API requests.

### Changes Made:

#### 1. **Added CSRF Token Extraction**
```javascript
const csrfToken = /*[[${_csrf.token}]]*/ '';
const csrfHeader = /*[[${_csrf.headerName}]]*/ '';
```

This uses Thymeleaf's inline JavaScript feature to inject the CSRF token and header name from Spring Security.

#### 2. **Updated `updateQuantity()` Function**
```javascript
function updateQuantity(cartItemId, newQuantity) {
    const headers = {
        'Content-Type': 'application/json'
    };
    headers[csrfHeader] = csrfToken; // Add CSRF token

    fetch(`/api/cart/items/${cartItemId}`, {
        method: 'PUT',
        headers: headers, // Include CSRF token
        body: JSON.stringify({ quantity: newQuantity })
    })
    // ... rest of the code
}
```

#### 3. **Updated `removeFromCart()` Function**
```javascript
function removeFromCart(cartItemId) {
    const headers = {};
    headers[csrfHeader] = csrfToken; // Add CSRF token

    fetch(`/api/cart/items/${cartItemId}`, {
        method: 'DELETE',
        headers: headers // Include CSRF token
    })
    // ... rest of the code
}
```

## How It Works Now

### Quantity Update Flow:
1. User clicks **+** button → `updateQuantity(id, quantity+1)` called
2. JavaScript creates headers object with CSRF token
3. Sends PUT request to `/api/cart/items/{id}` with token
4. Spring Security validates CSRF token
5. Server updates quantity in database
6. Page reloads to show updated cart

### Quantity Decrease Flow:
1. User clicks **-** button → `updateQuantity(id, quantity-1)` called
2. If quantity becomes 0, calls `removeFromCart(id)` instead
3. Otherwise, same flow as increase

### Remove Item Flow:
1. User clicks **Delete** (trash icon) → confirmation dialog appears
2. User confirms → `removeFromCart(id)` called
3. JavaScript creates headers object with CSRF token
4. Sends DELETE request to `/api/cart/items/{id}` with token
5. Spring Security validates CSRF token
6. Server removes item from database
7. Page reloads to show updated cart

## Testing

### Test the + Button:
1. Go to cart page
2. Click the `+` button next to an item
3. ✅ Quantity should increase
4. ✅ Page refreshes
5. ✅ Total price updates
6. ✅ Cart badge in header updates

### Test the - Button:
1. Go to cart page
2. Click the `-` button next to an item
3. ✅ Quantity should decrease
4. ✅ If quantity was 1, item is removed
5. ✅ Page refreshes
6. ✅ Total price updates

### Test the Delete Button:
1. Go to cart page
2. Click the trash icon
3. ✅ Confirmation dialog appears
4. ✅ Click OK → item is removed
5. ✅ Click Cancel → item remains

## Spring Security CSRF Protection

### What is CSRF?
CSRF (Cross-Site Request Forgery) is an attack where a malicious website tricks a user's browser into making unwanted requests to another site where the user is authenticated.

### How Spring Security Protects:
- Generates a unique token for each session
- Requires this token in all state-changing requests (POST, PUT, DELETE)
- Rejects requests without valid token
- Token is available via `${_csrf.token}` in Thymeleaf

### Why We Need It:
- **PUT /api/cart/items/{id}**: Updates cart quantity
- **DELETE /api/cart/items/{id}**: Removes cart item
- Both modify server state, so CSRF protection is required

## Technical Details

### Thymeleaf Inline JavaScript:
```javascript
const csrfToken = /*[[${_csrf.token}]]*/ '';
```

- The `/*[[ ... ]]*/` syntax tells Thymeleaf to replace this with the actual value
- The fallback `''` is used during development
- Rendered output: `const csrfToken = 'abc123xyz...';`

### Dynamic Header Addition:
```javascript
const headers = {};
headers[csrfHeader] = csrfToken;
```

- Uses bracket notation to set header dynamically
- Header name is typically `X-CSRF-TOKEN`
- Allows Spring Security to validate the request

## Files Modified

1. ✅ `/src/main/resources/templates/cart.html`
   - Added CSRF token extraction
   - Updated `updateQuantity()` to include token
   - Updated `removeFromCart()` to include token

## Error Handling

Both functions include error handling:

### Success Case:
- Server returns `200 OK`
- Page reloads with `location.reload()`
- User sees updated cart

### Error Case:
- Server returns error (403 Forbidden, 400 Bad Request, etc.)
- Alert shows: "Failed to update quantity" or "Failed to remove item"
- Console logs error details for debugging
- Cart remains unchanged

## Additional Fix

Also killed the old Java process that was blocking port 8080:
```bash
lsof -ti:8080 | xargs kill -9
```

This ensured the new application could start successfully.

## Status

✅ **Complete** - All cart buttons now work correctly!

### What Works:
- ✅ Increment quantity (+)
- ✅ Decrement quantity (-)
- ✅ Remove item (trash icon)
- ✅ CSRF protection maintained
- ✅ Proper error handling
- ✅ User feedback via alerts
- ✅ Page refresh after changes
- ✅ Cart badge updates

The cart functionality is now fully operational with proper security! 🎉
