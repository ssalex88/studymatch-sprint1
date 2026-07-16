// src/core/services/currentUserCache.js

/**
 * Persists only non-authoritative UI data for the current user.
 * Session authority lives in the backend HttpOnly cookie, never in localStorage.
 */
export function cacheCurrentUserForUi(user) {
	if (!user) {
		clearCurrentUserCache();
		return null;
	}

	const cachedUser = { ...user };
	delete cachedUser.sessionToken;

	localStorage.removeItem("sessionToken");
	localStorage.setItem("currentUser", JSON.stringify(cachedUser));

	return cachedUser;
}

export function clearCurrentUserCache() {
	localStorage.removeItem("currentUser");
	localStorage.removeItem("sessionToken");
}
