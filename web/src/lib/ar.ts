// The 8th Wall projects that WebARFragmentSimulation and WebARFragmentLearn loaded in a WebView
export type ArExperience = {
  id: string
  title: string
  text: string
  image: string
  url: string
  /** Markers on the sheet that start this experience */
  markers: string[]
}

// Same titles and wording as explore.xml
export const AR_EXPERIENCES: ArExperience[] = [
  {
    id: 'water-cycle',
    title: 'Water Cycle Process',
    text: 'Experience the simulation of the water cycle process in AR view',
    image: '/images/ar/water-cycle.png',
    url: 'https://ecoexplorer.8thwall.app/exoexplorer/',
    markers: ['Water Cycle'],
  },
  {
    id: 'learn',
    title: 'AR Learning',
    text: 'Explore and learn about the animals and plants in AR view',
    image: '/images/ar/learn-animals.png',
    url: 'https://ecoexplorer.8thwall.app/animals/',
    markers: ['Hercules Beetle', 'Hibiscus'],
  },
]

// The sheet ExploreFragment linked to on Google Drive, now served with the app
export const MARKER_SHEET_URL = '/ar/EcoExplorer_ARimage.pdf'
