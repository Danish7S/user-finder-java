const form = document.getElementById("searchForm");
const input = document.getElementById("username");
const button = document.getElementById("searchButton");
const statusMessage = document.getElementById("status");
const profileSection = document.getElementById("profileSection");

function setStatus(message, type = "") {
    statusMessage.textContent = message;
    statusMessage.className = `status ${type}`;
}

function setText(id, value, fallback = "Not specified") {
    document.getElementById(id).textContent =
        value == null || String(value).trim() === "" ? fallback : String(value);
}

function setExternalLink(id, value, fallback) {
    const element = document.getElementById(id);
    element.removeAttribute("href");
    element.removeAttribute("target");
    element.removeAttribute("rel");
    element.textContent = fallback;

    if (!value || !String(value).trim()) return;

    let url;
    try {
        url = new URL(String(value));
    } catch {
        return;
    }

    if (url.protocol !== "https:" && url.protocol !== "http:") return;

    element.href = url.href;
    element.target = "_blank";
    element.rel = "noopener noreferrer";
    element.textContent = value;
}

function formatDate(value) {
    if (!value) return "Not available";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "Not available";
    return date.toLocaleDateString("en-US", { month: "long", year: "numeric" });
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("en-US");
}

function showProfile(user) {
    const avatar = document.getElementById("avatar");
    avatar.onerror = () => {
        avatar.onerror = null;
        avatar.removeAttribute("src");
        avatar.alt = "Avatar unavailable";
    };
    avatar.src = user.avatarUrl || "";
    avatar.alt = `${user.login}'s GitHub avatar`;

    setText("name", user.name, user.login);
    setText("login", `@${user.login}`);
    setExternalLink("login", user.htmlUrl, `@${user.login}`);
    setText("bio", user.bio, "This user has not added a bio.");
    setText("location", user.location);
    setText("company", user.company);
    setExternalLink("blog", user.blog, "Not specified");
    setText("joined", formatDate(user.createdAt));
    setText("repos", formatNumber(user.publicRepos), "0");
    setText("followers", formatNumber(user.followers), "0");
    setText("following", formatNumber(user.following), "0");
    setExternalLink("profileLink", user.htmlUrl, "Profile unavailable");

    profileSection.hidden = false;
    setStatus(`Profile found: ${user.login}`, "success");
}

async function searchUser(rawUsername) {
    const username = rawUsername.trim();

    if (!/^[A-Za-z0-9-]{1,39}$/.test(username)) {
        profileSection.hidden = true;
        setStatus("Enter a valid GitHub username.", "error");
        input.focus();
        return;
    }

    button.disabled = true;
    button.textContent = "Searching...";
    profileSection.hidden = true;
    setStatus("Searching GitHub...");

    try {
        const response = await fetch(`/api/users/${encodeURIComponent(username)}`);
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.detail || data.message ||
                (response.status === 404
                    ? "GitHub username was not found."
                    : "Could not retrieve this profile. Please try again."));
        }

        showProfile(data);
    } catch (error) {
        setStatus(error.message || "Connection failed. Check your internet connection.", "error");
    } finally {
        button.disabled = false;
        button.textContent = "Search";
    }
}

form.addEventListener("submit", (event) => {
    event.preventDefault();
    searchUser(input.value);
});
