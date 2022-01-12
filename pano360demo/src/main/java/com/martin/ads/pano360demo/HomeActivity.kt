package com.martin.ads.pano360demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.martin.ads.vrlib.constant.MimeType
import com.martin.ads.vrlib.ui.Pano360ConfigBundle
import com.martin.ads.vrlib.ui.PanoPlayerActivity
import com.martin.ads.vrlib.utils.BitmapUtils
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import java.util.regex.Pattern

class HomeActivity : AppCompatActivity() {
	private val USE_DEFAULT_ACTIVITY = true

	@OptIn(ExperimentalPermissionsApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			// Track if the user doesn't want to see the rationale any more.
			var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

			val externalStoragePermissionState =
				rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
			PermissionRequired(
				permissionState = externalStoragePermissionState,
				permissionNotGrantedContent = {
					if (doNotShowRationale) {
						Text("Feature not available")
					} else {
						Column {
							Text("Accessing external files is important for this app. Please grant the permission.")
							Spacer(modifier = Modifier.height(8.dp))
							Row {
								Button(onClick = { externalStoragePermissionState.launchPermissionRequest() }) {
									Text("Ok!")
								}
								Spacer(Modifier.width(8.dp))
								Button(onClick = { doNotShowRationale = true }) {
									Text("Nope")
								}
							}
						}
					}
				},
				permissionNotAvailableContent = {
					Column {
						Text(
							"Write external storage settings declined"
						)
						Spacer(modifier = Modifier.height(8.dp))
						Button(onClick = { /** TODO **/ }) {
							Text("Open Settings")
						}
					}
				}
			) {
				HomeContent()
			}

		}
	}

	@Composable
	fun HomeContent() {
		var isChecked by remember { mutableStateOf(false) }
		Column {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(text = "Plane Mode")
				Checkbox(checked = isChecked, onCheckedChange = { isChecked = !isChecked })
			}
			CardView(Icons.Default.VideoLibrary, R.string.title_1, R.string.content_text_1) {
				start(
					"""android.resource://$packageName/${R.raw.demo_video}""",
					MimeType.RAW or MimeType.VIDEO,
					planeModeEnabled = isChecked
				)
			}
			CardView(Icons.Default.OpenInBrowser, R.string.title_2, R.string.content_text_2) {
				val intent = Intent(this@HomeActivity, FilePickerActivity::class.java)
				intent.putExtra(
					FilePickerActivity.ARG_FILTER,
					Pattern.compile("(.*\\.mp4$)||(.*\\.avi$)||(.*\\.wmv$)")
				)
				startActivityForResult(intent, 1)
			}
			CardView(Icons.Default.Theaters, R.string.title_3, R.string.content_text_3) {
				start(
					filePath = "images/vr_cinema.jpg",
					videoHotspotPath =
					"android.resource://" + packageName + "/" + R.raw.demo_video,
					mimeType = MimeType.ASSETS or MimeType.PICTURE
				)
			}
			CardView(Icons.Default.Panorama, R.string.title_4, R.string.content_text_4) {
				start(
					filePath = "images/texture_360_n.jpg",
					mimeType = MimeType.ASSETS or MimeType.PICTURE
				)
			}
			CardView(Icons.Default.Email, R.string.title_6, R.string.content_text_6) {

			}
		}
	}

	@OptIn(ExperimentalMaterialApi::class)
	@Composable
	private fun CardView(
		icon: ImageVector,
		@StringRes title: Int,
		@StringRes description: Int,
		modifier: Modifier = Modifier,
		onClick: () -> Unit
	) {
		Card(
			modifier = modifier
				.fillMaxWidth()
				.height(100.dp)
				.padding(10.dp), elevation = 10.dp, onClick = onClick
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Spacer(Modifier.width(8.dp))
				Image(imageVector = icon, contentDescription = null)
				Spacer(Modifier.width(8.dp))
				Column(verticalArrangement = Arrangement.Center) {
					Text(text = stringResource(id = title))
					Text(text = stringResource(id = description))
				}
			}
		}
	}

	@Preview
	@Composable
	private fun PreviewCardView() {
		CardView(
			icon = Icons.Default.LocalMovies,
			title = R.string.title_1,
			description = R.string.content_text_1
		) {

		}
	}

	private fun start(
		filePath: String,
		mimeType: Int,
		planeModeEnabled: Boolean = false,
		videoHotspotPath: String? = null
	) {
		val configBundle: Pano360ConfigBundle = Pano360ConfigBundle
			.newInstance()
			.setFilePath(filePath)
			.setMimeType(mimeType)
			.setPlaneModeEnabled(planeModeEnabled) //set it false to see default hotspot
			.setRemoveHotspot(true)
			.setVideoHotspotPath(videoHotspotPath)
		if (mimeType and MimeType.BITMAP != 0) {
			//add your own picture here
			// this interface may be removed in future version.
			configBundle.startEmbeddedActivityWithSpecifiedBitmap(
				this, BitmapUtils.loadBitmapFromRaw(this, R.mipmap.ic_launcher)
			)
			return
		}
		if (USE_DEFAULT_ACTIVITY) configBundle.startEmbeddedActivity(this) else {
			val intent = Intent(this, DemoWithGLSurfaceView::class.java)
			intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE, configBundle)
			startActivity(intent)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			val filePath = data?.getStringExtra(FilePickerActivity.RESULT_FILE_PATH).toString()
			val mimeType = MimeType.LOCAL_FILE or MimeType.VIDEO
			start(filePath, mimeType)
		}
	}
}
