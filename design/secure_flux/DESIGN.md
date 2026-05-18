---
name: Secure Flux
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#444654'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#757686'
  outline-variant: '#c5c5d7'
  surface-tint: '#364ed8'
  primary: '#0429ba'
  on-primary: '#ffffff'
  primary-container: '#2e47d1'
  on-primary-container: '#c5cbff'
  inverse-primary: '#bbc3ff'
  secondary: '#4648d4'
  on-secondary: '#ffffff'
  secondary-container: '#6063ee'
  on-secondary-container: '#fffbff'
  tertiary: '#762600'
  on-tertiary: '#ffffff'
  tertiary-container: '#9d3500'
  on-tertiary-container: '#ffc0aa'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dee0ff'
  primary-fixed-dim: '#bbc3ff'
  on-primary-fixed: '#000f5d'
  on-primary-fixed-variant: '#1432c0'
  secondary-fixed: '#e1e0ff'
  secondary-fixed-dim: '#c0c1ff'
  on-secondary-fixed: '#07006c'
  on-secondary-fixed-variant: '#2f2ebe'
  tertiary-fixed: '#ffdbce'
  tertiary-fixed-dim: '#ffb59a'
  on-tertiary-fixed: '#380d00'
  on-tertiary-fixed-variant: '#802a00'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-currency:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 34px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
  numeric-data:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base-unit: 4px
  margin-mobile: 20px
  gutter: 16px
  touch-target-min: 48px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style

This design system is engineered for high-stakes financial environments where precision and trust are paramount. The brand personality is **reliable, precise, and transparent**, prioritizing functional clarity over decorative flair. 

The visual direction follows a **Corporate / Modern** aesthetic with a "Bright" theme. It utilizes a pristine white base to evoke cleanliness and organization, paired with a deep, authoritative Indigo to signal security. The user interface minimizes cognitive load by using generous whitespace, ensuring that every interaction—especially those involving money handling—is intentional and error-free. The emotional response should be one of total confidence in the system's accuracy and speed.

## Colors

The palette is anchored by a vibrant yet secure **Indigo (#2E47D1)** as the primary brand color, used for critical actions and brand reinforcement. 

- **Primary:** Used for the main "Tap to Pay" or "Read NFC" actions and primary navigation.
- **Surface:** A pure white background (#FFFFFF) is mandatory to maintain the "Bright" theme and ensure maximum contrast for financial data.
- **Semantic States:**
    - **Success (Green):** Confirmed transactions and successful NFC reads.
    - **Pending (Amber):** Transactions in progress or synchronization states.
    - **Failed (Red):** Declined payments or NFC read errors.
    - **Reversed (Gray):** Cancelled or rolled-back transactions.
- **Neutrals:** A sophisticated range of slates and grays are used for secondary text and borders to maintain a professional, de-cluttered look.

## Typography

The typography system uses **Inter** for its exceptional legibility and neutral, systematic tone. 

- **Financial Data:** Currency amounts use the `display-currency` role with tight letter spacing for maximum impact. For lists and tables, use the `numeric-data` role which enables tabular figures (monospaced numbers) to ensure decimal points align vertically for easy scanning.
- **Hierarchy:** Clear distinction is made between transaction labels (Body-md) and the associated values (Numeric-data).
- **Readability:** High contrast ratios are maintained throughout. Avoid light weights; use Regular (400) for prose and Semi-bold (600) or Bold (700) for data and headings to ensure clarity in high-glare outdoor environments where mobile payments often occur.

## Layout & Spacing

The layout philosophy follows a strict **Fluid Grid** model optimized for one-handed mobile use. 

- **Safety:** To prevent accidental taps during money handling, the minimum touch target for any interactive element is set to **48px**. 
- **Margins:** A consistent 20px margin is applied to the left and right of the screen to provide visual breathing room and prevent thumb-fatigue.
- **Rhythm:** An 8px linear scale (4, 8, 16, 24, 32, 48, 64) governs all padding and margins to create a disciplined, structured interface.
- **Grouping:** Use `stack-lg` (24px) to separate distinct functional sections (e.g., Balance vs. Recent Transactions) and `stack-sm` (8px) for internal component spacing (e.g., Icon to Text).

## Elevation & Depth

This design system utilizes **Tonal Layers** and **Low-contrast outlines** rather than heavy shadows to maintain its clean, modern professional aesthetic.

- **Flat Foundation:** The primary background is at 0dp elevation.
- **Cards:** Transaction items and balance summaries sit on "Surface Level 1," which is defined by a subtle 1px border (#E2E8F0) and no shadow. This creates a "sheet" effect that feels organized and technical.
- **Modals & Overlays:** For NFC reading states and critical confirmations, use a high-elevation state with a soft, neutral ambient shadow (Blur: 16px, Spread: 0, Opacity: 8% Black) to focus the user's attention.
- **State Feedback:** Active states for buttons should use a slight darken-overlay rather than a depth change, keeping the UI feeling stable and grounded.

## Shapes

The shape language is **Rounded**, strike a balance between modern friendliness and professional rigidity.

- **Standard Elements:** Buttons, input fields, and transaction cards use a 0.5rem (8px) corner radius. This provides a modern feel without looking "bubbly" or toy-like.
- **Large Containers:** Bottom sheets and large dashboard cards use a 1rem (16px) radius for the top corners to soften the transition from the screen edge.
- **Status Badges:** Small indicators (e.g., "Paid", "Pending") use a full pill-shape (circular ends) to distinguish them from interactive buttons.

## Components

- **Action Buttons:** Primary buttons are large (56px height) with full-width Indigo fills. Text is centered, bold, and White. Secondary buttons use an Indigo outline with 1px thickness.
- **Transaction Cards:** Individual list items feature a leading icon slot (status-colored), a title/subtitle stack for the merchant/category, and a trailing stack for the currency amount and timestamp.
- **Balance Display:** A hero component at the top of the main screen. Large display-currency font, clearly labeled "Available Balance," with a subtle background tint of the primary color at 5% opacity to anchor the top of the hierarchy.
- **NFC Reading State:** A dedicated full-screen or bottom-sheet overlay. It must include a high-quality vector illustration or animation of the tap gesture, a clear "Ready to Scan" headline, and a progress indicator (circular or haptic feedback pulses).
- **Status Chips:** Small, non-interactive badges using the semantic color palette. Use a "ghost" style: 10% opacity fill of the semantic color with 100% opacity text of the same color for high legibility.
- **Input Fields:** Financial inputs must use a clean, outlined style with a clear "Focus" state in Indigo. Use a numeric-only keyboard by default for all amount entries.