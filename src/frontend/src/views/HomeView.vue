<template>
  <div class="home-view">
    <ThemeToggle />
    
    <!-- Navigation Header -->
    <header class="home-header">
      <div class="header-content">
        <div class="logo-section">
          <div class="logo-icon">
            <svg width="40" height="40" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="30" stroke="currentColor" stroke-width="2"/>
              <path d="M20 32h8l4-8h8l4 8h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="20" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
              <circle cx="44" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
              <path d="M26 44h12" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h1 class="app-title">RideWithUs</h1>
        </div>
        <nav class="header-nav">
          <button @click="scrollToSection('features')" class="nav-link">Features</button>
          <button @click="scrollToSection('about')" class="nav-link">About</button>
          <button @click="scrollToSection('contact')" class="nav-link">Contact</button>
          <div class="auth-buttons">
            <router-link to="/login" class="btn-secondary">Login</router-link>
            <router-link to="/register" class="btn-primary">Get Started</router-link>
          </div>
        </nav>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            Your Journey, 
            <span class="gradient-text">Our Priority</span>
          </h1>
          <p class="hero-subtitle">
            Experience the future of urban mobility with our smart bike-sharing platform. 
            Find, unlock, and ride bikes anywhere in the city with just a tap.
          </p>
          <div class="hero-actions">
            <router-link to="/register" class="btn-primary large">
              <span class="btn-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="8.5" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                  <line x1="20" y1="8" x2="20" y2="14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <line x1="23" y1="11" x2="17" y2="11" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </span>
              Start Your Journey
            </router-link>
            <router-link to="/login" class="btn-secondary large">
              <span class="btn-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="10,17 15,12 10,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="15" y1="12" x2="3" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </span>
              Sign In
            </router-link>
          </div>
        </div>
        <div class="hero-image">
          <div class="bike-showcase">
            <div class="bike-card">
              <div class="bike-icon">
                <svg width="80" height="80" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="32" cy="32" r="30" stroke="currentColor" stroke-width="2"/>
                  <path d="M20 32h8l4-8h8l4 8h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <circle cx="20" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                  <circle cx="44" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                  <path d="M26 44h12" stroke="currentColor" stroke-width="2"/>
                </svg>
              </div>
              <h3>Smart Bikes</h3>
              <p>GPS-enabled, IoT-connected bikes for seamless tracking</p>
            </div>
            <div class="bike-card">
              <div class="bike-icon">
                <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
              </div>
              <h3>Smart Stations</h3>
              <p>Convenient pickup and drop-off locations across the city</p>
            </div>
            <div class="bike-card">
              <div class="bike-icon">
                <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M22 12h-4l-3 9L9 3l-3 9H2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3>Real-time Analytics</h3>
              <p>Track your rides, distance, and environmental impact</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="features-section">
      <div class="features-content">
        <h2 class="section-title">Why Choose RideWithUs?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <circle cx="8" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                <circle cx="16" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h3>Easy Access</h3>
            <p>Find and unlock bikes instantly with our mobile app. No complicated setup required.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h3>City-Wide Coverage</h3>
            <p>Hundreds of stations strategically placed throughout the city for maximum convenience.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 17l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Smart Technology</h3>
            <p>GPS tracking, IoT sensors, and real-time monitoring for the safest ride experience.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Track Your Impact</h3>
            <p>Monitor your carbon footprint and contribute to a greener, more sustainable city.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="16" r="1" fill="currentColor"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h3>Secure & Safe</h3>
            <p>Advanced security features and regular maintenance ensure your safety on every ride.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Community Driven</h3>
            <p>Join thousands of riders who are transforming urban mobility and building healthier cities.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section id="about" class="about-section">
      <div class="about-content">
        <div class="about-text">
          <h2 class="section-title">About RideWithUs</h2>
          <p class="about-description">
            RideWithUs is revolutionizing urban transportation through innovative bike-sharing technology. 
            Our mission is to make sustainable mobility accessible to everyone while reducing traffic congestion 
            and environmental impact in cities worldwide.
          </p>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">50K+</div>
              <div class="stat-label">Active Riders</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">500+</div>
              <div class="stat-label">Bike Stations</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">1M+</div>
              <div class="stat-label">Miles Ridden</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">100K+</div>
              <div class="stat-label">CO₂ Saved (kg)</div>
            </div>
          </div>
        </div>
        <div class="about-image">
          <div class="image-placeholder">
            <svg width="200" height="200" viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="100" cy="100" r="90" stroke="currentColor" stroke-width="2"/>
              <path d="M60 100h20l10-20h20l10 20h20" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
              <circle cx="60" cy="140" r="15" stroke="currentColor" stroke-width="2"/>
              <circle cx="140" cy="140" r="15" stroke="currentColor" stroke-width="2"/>
              <path d="M80 140h40" stroke="currentColor" stroke-width="2"/>
              <circle cx="100" cy="60" r="8" stroke="currentColor" stroke-width="2"/>
              <path d="M100 68v20" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-content">
        <h2 class="cta-title">Ready to Start Your Journey?</h2>
        <p class="cta-subtitle">
          Join thousands of riders who are already making a difference in their cities.
        </p>
        <div class="cta-actions">
          <router-link to="/register" class="btn-primary large">
            <span class="btn-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="8.5" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                <line x1="20" y1="8" x2="20" y2="14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <line x1="23" y1="11" x2="17" y2="11" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </span>
            Get Started Today
          </router-link>
          <router-link to="/login" class="btn-secondary large">
            <span class="btn-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="10,17 15,12 10,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="15" y1="12" x2="3" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </span>
            Sign In
          </router-link>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer id="contact" class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <div class="footer-logo">
            <div class="logo-icon">
              <svg width="32" height="32" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="32" cy="32" r="30" stroke="currentColor" stroke-width="2"/>
                <path d="M20 32h8l4-8h8l4 8h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <circle cx="20" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                <circle cx="44" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                <path d="M26 44h12" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h3>RideWithUs</h3>
          </div>
          <p>Your journey, our priority. Transforming urban mobility one ride at a time.</p>
        </div>
        <div class="footer-section">
          <h4>Quick Links</h4>
          <ul>
            <li><router-link to="/login">Login</router-link></li>
            <li><router-link to="/register">Register</router-link></li>
            <li><a href="#features">Features</a></li>
            <li><a href="#about">About</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>Contact</h4>
          <ul>
            <li>support@ridewithus.com</li>
            <li>+1 (555) 123-4567</li>
            <li>123 Bike Street, City</li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 RideWithUs. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { onMounted } from 'vue'
import ThemeToggle from '../components/ThemeToggle.vue'

export default {
  name: 'HomeView',
  components: {
    ThemeToggle
  },
  setup() {
    const scrollToSection = (sectionId) => {
      const element = document.getElementById(sectionId)
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' })
      }
    }

    onMounted(() => {
      // Add any initialization logic here
    })

    return {
      scrollToSection
    }
  }
}
</script>

<style scoped>
.home-view {
  min-height: 100vh;
  background: var(--gradient);
  position: relative;
}

/* Header Styles */
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border);
  z-index: 1000;
  transition: all 0.3s ease;
}

/* Theme Toggle Positioning */
.home-view :deep(.theme-toggle) {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1001;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 8px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  color: var(--primary);
}

.app-title {
  font-size: 24px;
  font-weight: 800;
  background: var(--gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  background: none;
  border: none;
  color: var(--text);
  font-weight: 600;
  cursor: pointer;
  transition: color 0.3s ease;
  padding: 8px 0;
}

.nav-link:hover {
  color: var(--primary);
}

.auth-buttons {
  display: flex;
  gap: 12px;
}

.btn-primary, .btn-secondary {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background: var(--gradient);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(236, 72, 153, 0.3);
}

.btn-secondary {
  background: var(--surface);
  color: var(--text);
  border: 2px solid var(--border);
}

.btn-secondary:hover {
  background: var(--surface-hover);
  transform: translateY(-2px);
}

.btn-primary.large, .btn-secondary.large {
  padding: 16px 32px;
  font-size: 16px;
}

.btn-icon {
  display: flex;
  align-items: center;
}

/* Hero Section */
.hero-section {
  padding: 120px 20px 80px;
  min-height: 100vh;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  margin: 0 0 24px 0;
  color: white;
}

.gradient-text {
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  line-height: 1.6;
  margin: 0 0 32px 0;
  color: rgba(255, 255, 255, 0.9);
}

.hero-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.bike-showcase {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  max-width: 400px;
  width: 100%;
}

.bike-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: all 0.3s ease;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.bike-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.15);
}

.bike-icon {
  color: white;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.bike-card h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: white;
  line-height: 1.3;
}

.bike-card p {
  font-size: 14px;
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.4;
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Features Section */
.features-section {
  padding: 80px 20px;
  background: var(--surface);
  color: var(--text);
  position: relative;
  overflow: hidden;
}

.features-content {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 36px;
  font-weight: 800;
  text-align: center;
  margin: 0 0 60px 0;
  color: var(--text);
  position: relative;
  z-index: 2;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 32px;
  position: relative;
  z-index: 2;
}

.feature-card {
  background: var(--background);
  border: 2px solid var(--border);
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  margin: 0;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--card-shadow);
  border-color: var(--primary);
}

.feature-icon {
  color: var(--primary);
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: var(--text);
}

.feature-card p {
  font-size: 16px;
  line-height: 1.6;
  margin: 0;
  color: var(--text-secondary);
}

/* About Section */
.about-section {
  padding: 80px 20px;
  background: var(--gradient);
  margin: 0;
  position: relative;
}

.about-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.about-text .section-title {
  color: white;
  text-align: left;
  margin-bottom: 24px;
}

.about-description {
  font-size: 18px;
  line-height: 1.6;
  margin: 0 0 40px 0;
  color: rgba(255, 255, 255, 0.9);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: white;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
}

.about-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-placeholder {
  color: rgba(255, 255, 255, 0.3);
}

/* CTA Section */
.cta-section {
  padding: 80px 20px;
  background: var(--surface);
  text-align: center;
}

.cta-content {
  max-width: 800px;
  margin: 0 auto;
}

.cta-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 16px 0;
  color: var(--text);
}

.cta-subtitle {
  font-size: 20px;
  margin: 0 0 40px 0;
  color: var(--text-secondary);
}

.cta-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

/* Footer */
.footer {
  background: var(--surface);
  color: var(--text);
  padding: 60px 20px 20px;
  border-top: 1px solid var(--border);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.footer-logo .logo-icon {
  color: var(--primary);
}

.footer-logo h3 {
  font-size: 20px;
  font-weight: 800;
  margin: 0;
  color: var(--text);
}

.footer-section p {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 16px;
}

.footer-section h4 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: var(--text);
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: 8px;
}

.footer-section a {
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: 16px;
}

.footer-section a:hover {
  color: var(--primary);
}

.footer-bottom {
  border-top: 1px solid var(--border);
  padding-top: 20px;
  text-align: center;
}

.footer-bottom p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
}

/* Mobile-first responsive design */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 16px 20px;
    gap: 16px;
  }
  
  .header-nav {
    flex-direction: column;
    gap: 16px;
  }
  
  .auth-buttons {
    width: 100%;
    justify-content: center;
  }
  
  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .bike-showcase {
    max-width: 100%;
    padding: 0 20px;
  }
  
  .bike-card {
    min-height: 180px;
    padding: 20px;
  }
  
  .about-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .footer {
    padding: 40px 20px 20px;
  }
  
  .footer-content {
    gap: 30px;
  }
  
  .footer-section p,
  .footer-section a {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 28px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .cta-title {
    font-size: 28px;
  }
  
  .bike-card {
    min-height: 160px;
    padding: 16px;
  }
  
  .bike-card h3 {
    font-size: 16px;
  }
  
  .bike-card p {
    font-size: 13px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .btn-primary.large, .btn-secondary.large {
    padding: 14px 24px;
    font-size: 14px;
  }
}
</style>
