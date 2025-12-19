import React, {useState} from 'react'
import styles from './Label.module.css'

const LabelComponent = ({ labels, labelName }) => {
  const [showAll, setShowAll] = useState(false);

  const displayedItems = showAll ? labels : labels.slice(0, 1);

  const handleToggleShowAll = () => {
    setShowAll(!showAll);
  }

  return (
    <div className={styles.list}>
      <ul>
        <p>{labelName}</p>
        {
          // Use (item, i) to get index and ensure that list items have a key
          displayedItems.map((item, i) =>
            <li key={i}>{item}</li>)
        }
        {
          labels.length > 1 && (
            <button className={styles.toggleButton} onClick={handleToggleShowAll}>
              {showAll ? 'Less' : 'More'}
            </button>
          )
        }
      </ul>
    </div>
  )
}

export default LabelComponent